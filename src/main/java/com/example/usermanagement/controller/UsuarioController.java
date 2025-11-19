package com.example.usermanagement.controller;

import com.example.usermanagement.dto.ResultadoImportacaoDTO;
import com.example.usermanagement.model.Usuario;
import com.example.usermanagement.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
    private final UsuarioService usuarioService;

    // Injeção via construtor
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ---------- Helpers ----------
    private void addSuccess(RedirectAttributes redirectAttributes, String mensagem) {
        redirectAttributes.addFlashAttribute("msgSucesso", mensagem);
    }

    private void addError(RedirectAttributes redirectAttributes, String mensagem) {
        redirectAttributes.addFlashAttribute("erro", mensagem);
    }

    // ---------- Rotas ----------

    // Página de listagem com paginação e busca
    @GetMapping
    public String listarUsuarios(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "busca", required = false) String busca,
            Model model) {

        Page<Usuario> resultado = usuarioService.listar(page, size, busca);
        model.addAttribute("usuariosPage", resultado);
        model.addAttribute("busca", busca);
        return "usuarios_list";
    }

    // Formulário de criação
    @GetMapping("/novo")
    public String novoUsuarioForm(Model model) {
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new Usuario());
        }
        return "usuario_form";
    }

    // Salvar novo usuário
    @PostMapping("/salvar")
    public String salvarUsuario(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("usuario", usuario);
            return "usuario_form";
        }

        try {
            usuarioService.criarUsuario(usuario);
            addSuccess(redirectAttributes, "Usuário criado com sucesso.");
            logger.info("Usuário criado: {}", usuario.getEmail());
            return "redirect:/usuarios";
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao criar usuário: {}", e.getMessage());
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("usuario", usuario);
            return "usuario_form";
        } catch (Exception e) {
            logger.error("Erro inesperado ao criar usuário", e);
            model.addAttribute("erro", "Erro inesperado ao criar usuário.");
            model.addAttribute("usuario", usuario);
            return "usuario_form";
        }
    }

    // Editar (abrir formulário)
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> u = usuarioService.buscarPorId(id);
        if (u.isEmpty()) {
            addError(redirectAttributes, "Usuário não encontrado.");
            return "redirect:/usuarios";
        }
        model.addAttribute("usuario", u.get());
        return "usuario_form";
    }

    // Atualizar usuário
    @PostMapping("/atualizar/{id}")
    public String atualizarUsuario(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("usuario", usuario);
            return "usuario_form";
        }

        try {
            usuarioService.atualizarUsuario(id, usuario);
            addSuccess(redirectAttributes, "Usuário atualizado com sucesso.");
            logger.info("Usuário atualizado: id={}, email={}", id, usuario.getEmail());
            return "redirect:/usuarios";
        } catch (IllegalArgumentException e) {
            logger.warn("Falha ao atualizar usuário: {}", e.getMessage());
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("usuario", usuario);
            return "usuario_form";
        } catch (Exception e) {
            logger.error("Erro inesperado ao atualizar usuário", e);
            model.addAttribute("erro", "Erro inesperado ao atualizar usuário.");
            model.addAttribute("usuario", usuario);
            return "usuario_form";
        }
    }

    // Excluir com confirmação via JS (endpoint)
    @PostMapping("/remover/{id}")
    public String removerUsuario(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.removerUsuario(id);
            addSuccess(redirectAttributes, "Usuário removido com sucesso.");
            logger.info("Usuário removido: id={}", id);
        } catch (Exception e) {
            addError(redirectAttributes, "Erro ao remover usuário: " + e.getMessage());
            logger.error("Erro ao remover usuário id={}", id, e);
        }
        return "redirect:/usuarios";
    }

    // Página de importação (GET) - exibe resultado vindo via flash (após POST)
    @GetMapping("/importar")
    public String importarForm(Model model) {
        if (!model.containsAttribute("resultado")) {
            // garante que o fragment encontre a variável (pode ser null)
            model.addAttribute("resultado", null);
        }
        return "importar_excel";
    }

    // Endpoint para importação (POST) - valida + processa e redireciona para GET com flash
    @PostMapping("/importar")
    public String importarArquivo(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        // Validações simples do arquivo
        if (file == null || file.isEmpty()) {
            addError(redirectAttributes, "Por favor selecione um arquivo .xlsx para importar.");
            return "redirect:/usuarios/importar";
        }
        String nome = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        if (!nome.endsWith(".xlsx")) {
            addError(redirectAttributes, "Formato inválido. Envie um arquivo .xlsx.");
            return "redirect:/usuarios/importar";
        }

        try {
            ResultadoImportacaoDTO resultado = usuarioService.importarUsuariosExcel(file);
            // passa o resultado via flash para a página GET /importar exibir
            redirectAttributes.addFlashAttribute("resultado", resultado);
            addSuccess(redirectAttributes, "Importação finalizada. Verifique possíveis erros abaixo.");
            logger.info("Importação concluída: arquivo={}, totalLinhas={}, inseridos={}",
                    file.getOriginalFilename(), resultado.getTotalLinhas(), resultado.getInseridos());
            return "redirect:/usuarios/importar";
        } catch (Exception e) {
            logger.error("Erro ao importar arquivo", e);
            addError(redirectAttributes, "Erro ao processar o arquivo: " + e.getMessage());
            return "redirect:/usuarios/importar";
        }
    }

    // Endpoints REST simples (JSON) - opcional para uso por frontend separado
    @GetMapping("/api/{id}")
    @ResponseBody
    public Usuario buscarPorIdApi(@PathVariable("id") Long id) {
        return usuarioService.buscarPorId(id).orElse(null);
    }
}
