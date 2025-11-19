package com.example.usermanagement.service;

import com.example.usermanagement.dto.ResultadoImportacaoDTO;
import com.example.usermanagement.model.Usuario;
import com.example.usermanagement.repository.UsuarioRepository;
import com.example.usermanagement.util.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Page<Usuario> listar(int pagina, int tamanho, String busca) {
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("dataCriacao").descending());
        if (busca == null || busca.isBlank()) {
            return usuarioRepository.findAll(pageable);
        } else {
            return usuarioRepository.findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(busca, busca, pageable);
        }
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario criarUsuario(Usuario usuario) {
        // Validações simples
        if (usuario.getNome() == null || usuario.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizarUsuario(Long id, Usuario dados) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        // se trocar email, verificar unicidade
        if (!existente.getEmail().equalsIgnoreCase(dados.getEmail())) {
            if (usuarioRepository.findByEmail(dados.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email já cadastrado");
            }
            existente.setEmail(dados.getEmail());
        }
        existente.setNome(dados.getNome());
        existente.setStatus(dados.getStatus());
        return usuarioRepository.save(existente);
    }

    public void removerUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public ResultadoImportacaoDTO importarUsuariosExcel(MultipartFile file) {
        ResultadoImportacaoDTO resultado = new ResultadoImportacaoDTO();
        try (InputStream is = file.getInputStream()) {
            List<Usuario> usuarios = ExcelUtil.lerUsuariosDoExcel(is);
            resultado.setTotalLinhas(usuarios.size());
            int inseridos = 0;
            int linha = 1; // para relatórios (considerando primeira linha de dados = linha 2 do arquivo)
            for (Usuario u : usuarios) {
                linha++;
                // Validações
                if (u.getNome() == null || u.getNome().isBlank()) {
                    resultado.addErro("Linha " + linha + ": nome vazio");
                    continue;
                }
                if (u.getEmail() == null || u.getEmail().isBlank()) {
                    resultado.addErro("Linha " + linha + ": email vazio");
                    continue;
                }
                // Verifica formato simples de email
                if (!u.getEmail().contains("@")) {
                    resultado.addErro("Linha " + linha + ": email inválido (" + u.getEmail() + ")");
                    continue;
                }
                // Verifica email duplicado
                if (usuarioRepository.findByEmail(u.getEmail()).isPresent()) {
                    resultado.addErro("Linha " + linha + ": email já existe (" + u.getEmail() + ")");
                    continue;
                }
                // salvar
                usuarioRepository.save(u);
                inseridos++;
            }
            resultado.setInseridos(inseridos);
        } catch (Exception e) {
            resultado.addErro("Erro ao processar arquivo: " + e.getMessage());
        }
        return resultado;
    }
}
