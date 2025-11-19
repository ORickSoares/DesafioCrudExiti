# 📘 Gerenciamento de Usuários — Projeto Completo (Java + Spring Boot + PostgreSQL + Thymeleaf)

> _Desenvolvido como estudo prático para aprimorar minhas habilidades em backend, frontend, integração com banco de dados e boas práticas de desenvolvimento._

---

## 📝 1. Sobre o projeto

Este é um **sistema completo de Gerenciamento de Usuários**, criado com o objetivo de simular um projeto real, explorando desde o backend estruturado até a interface web com modo dark e importação via Excel, com o objetivo de resolver um desafio de CRUD proposto pela empresa de tecnologoa EXITI.

Eu construí cada parte pensando tanto na experiência do usuário quanto em boas práticas de engenharia de software. O projeto serve como base de estudo, portfólio e preparação para entrevista técnica.

---

## 🛠 2. Tecnologias utilizadas

### **Backend**

- Java **21**
- Spring Boot (Web, JPA, Validation)
- PostgreSQL
- Apache POI (para Excel)

### **Frontend**

- Thymeleaf
- HTML5 + CSS3 moderno (tema dark + variáveis CSS)
- JavaScript (persistência de tema e confirmações)

### **Build / Config**

- Maven
- `application.properties`
- Estrutura organizada em MVC

---

## 🧩 3. Arquitetura do Projeto

```
src/main/java/com/example/usermanagement/
 ├── controller/          → Controladores, rotas e fluxo
 ├── service/             → Regras de negócio e validação
 ├── repository/          → JPA Repository
 ├── model/               → Entidade Usuario
 ├── dto/                 → ResultadoImportacaoDTO
 ├── util/                → ExcelUtil (Apache POI)
 └── UserManagementApplication.java (Main)
```

**Views (Thymeleaf)**

```
src/main/resources/templates/
 ├── usuarios_list.html
 ├── usuario_form.html
 ├── importar_excel.html
 └── fragments/
      └── alerts.html
```

**Recursos estáticos**

```
src/main/resources/static/
 ├── css/style.css
 └── js/script.js
```

---

## 🌐 4. Endpoints — Documentação Completa

### **Interface Web (HTML / Thymeleaf)**

| Método | Rota                       | Ação                  |
| ------ | -------------------------- | --------------------- |
| GET    | `/usuarios`                | Lista paginada        |
| GET    | `/usuarios/novo`           | Formulário de criação |
| POST   | `/usuarios/salvar`         | Salva novo usuário    |
| GET    | `/usuarios/editar/{id}`    | Editar usuário        |
| POST   | `/usuarios/atualizar/{id}` | Atualiza              |
| POST   | `/usuarios/remover/{id}`   | Remove                |
| GET    | `/usuarios/importar`       | Página de importação  |
| POST   | `/usuarios/importar`       | Importa Excel         |

### **API REST (JSON)**

| Método | Rota                 | Ação         |
| ------ | -------------------- | ------------ |
| GET    | `/usuarios/api/{id}` | Retorna JSON |

---

## 🧠 5. Backend — Funcionamento Técnico

### **Controller**

Responsável por:

- Receber requisições
- Encaminhar dados para o Service
- Aplicar RedirectAttributes
- Configurar paginação e busca
- Manter mensagens de sucesso/erro após operações

### **Service**

Aqui estão as regras de negócio:

- Criação, edição, exclusão de usuários
- Validações únicas (ex.: email duplicado)
- Processamento do Excel com relatório de erros
- Sanitização de dados

### **Repository**

Baseado em Spring Data JPA:

```
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Page<Usuario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
```

### **Importação Excel (Apache POI)**

Desafios resolvidos:

- Tipos inconsistentes de células
- Planilhas com colunas vazias
- Erros tratados linha a linha
- Relatório detalhado de importação

Resultado final é um sistema resiliente e bem testado.

---

## 🎨 6. Frontend — Detalhes Técnicos

### **Thymeleaf**

- Organização com fragments reutilizáveis
- Campos ligados via `th:object`
- Tabela responsiva com paginação
- Datas exibidas com `#temporals.format`
- Integração perfeita com flash messages

### **CSS moderno (com modo dark)**

Características:

- 100% baseado em variáveis CSS
- Modo claro e escuro consistentes
- Alto contraste
- Responsividade real
- Comentários técnicos detalhados

### **JavaScript**

Funções implementadas:

- Persistência do tema no `localStorage`
- Aplicação imediata do tema antes do CSS (sem flicker)
- Modal de confirmação de exclusão

---

## 🌑 7. Modo Dark — Explicação Técnica

O dark mode funciona via `data-theme="dark"` no elemento `<html>`.

### Como evitar piscar na troca de tema (FOUT)

Antes do CSS carregar:

```
(function(){
  const t = localStorage.getItem('theme');
  if (t === 'dark')
    document.documentElement.setAttribute('data-theme', 'dark');
})();
```

Assim o navegador já inicia na versão correta.

---

## 🧭 8. Minha Jornada e Dificuldades (técnico + humano)

### 🧩 Problemas com Thymeleaf

Tive que lidar com erros como:

- `Method format cannot be found`
- `Could not parse expression`
- `BindingResult not found for attribute`

Aprendi que:

- Expressões complexas precisam estar **dentro de `${}`**
- Fragments não devem usar variáveis que não existem no contexto

### 📦 Apache POI me fez suar

- Células numéricas tratadas como texto
- Verificações de `null`
- Planilhas mal formatadas

No fim, consegui um importador robusto e à prova de falhas.

### 🌑 Dark mode piscando

Resolvido ao aplicar o tema antes do CSS (técnica fundamental de performance).

### 🎯 Conclusões pessoais

Esse projeto me ensinou:

- a dominar melhor Spring MVC
- ler logs com cuidado
- refatorar controllers para ficarem profissionais
- criar CSS limpo e escalável
- resolver problemas reais do dia a dia de um dev

---

## 🚀 9. Como rodar o projeto

### 1. Criar Banco no PostgreSQL

```
CREATE DATABASE user_management;
```

### 2. Configurar `application.properties`

```
spring.datasource.url=jdbc:postgresql://localhost:5432/user_management
spring.datasource.username=postgres
spring.datasource.password=SUASENHA

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.thymeleaf.cache=false
```

### 3. Rodar

```
mvn clean package
mvn spring-boot:run
```

Acessar: **http://localhost:8080/usuarios**

---

## 🧪 10. Testes realizados

### CRUD

- Criar
- Editar
- Remover (com JS)
- Paginação
- Busca

### Excel

- Importação com validação
- Relatório completo

### Tema

- Dark mode persistente
- Responsividade revisada

---

## 📌 11. Considerações Finais

Este projeto consolidou minha base em:

- Backend moderno com Spring Boot
- Integração real com PostgreSQL
- Thymeleaf avançado
- Design escalável com CSS
- Boas práticas de controller/service
- Manipulação de Excel com Apache POI

Mais importante: representa meu avanço como desenvolvedor e minha capacidade de resolver problemas complexos com paciência e engenharia.
