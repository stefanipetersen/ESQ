package br.com.fiap.esq.service;

import br.com.fiap.esq.dto.PerfilRequest;
import br.com.fiap.esq.dto.SenhaRequest;
import br.com.fiap.esq.exception.CredenciaisInvalidasException;
import br.com.fiap.esq.exception.EntidadeNaoEncontradaException;
import br.com.fiap.esq.exception.RegraNegocioException;
import br.com.fiap.esq.model.Usuario;
import br.com.fiap.esq.repository.ContaRepository;
import br.com.fiap.esq.repository.InvestimentoRepository;
import br.com.fiap.esq.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;
    private final InvestimentoRepository investimentoRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, ContaRepository contaRepository,
                          InvestimentoRepository investimentoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.contaRepository = contaRepository;
        this.investimentoRepository = investimentoRepository;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario buscar(int id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));
    }

    public Usuario autenticar(String dsEmail, String senha) {
        return usuarioRepository.findByDsEmail(dsEmail)
                .filter(u -> u.getHashSenha().equals(senha))
                .orElseThrow(() -> new CredenciaisInvalidasException("E-mail ou senha inválidos"));
    }

    public Usuario cadastrar(Usuario usuario) {
        if (usuarioRepository.existsByDsEmail(usuario.getDsEmail()))
            throw new RegraNegocioException("E-mail já cadastrado");
        usuario.setIdUsuario(0);
        usuario.setDtCadastro(LocalDate.now());
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizar(int id, Usuario dados) {
        Usuario atual = buscar(id);
        if (usuarioRepository.existsByDsEmailAndIdUsuarioNot(dados.getDsEmail(), id))
            throw new RegraNegocioException("E-mail já cadastrado");
        atual.setNmUsuario(dados.getNmUsuario());
        atual.setDsEmail(dados.getDsEmail());
        atual.setHashSenha(dados.getHashSenha());
        if (dados.getNrTelefone() != null) atual.setNrTelefone(dados.getNrTelefone());
        if (dados.getDtNascimento() != null) atual.setDtNascimento(dados.getDtNascimento());
        return usuarioRepository.save(atual);
    }

    //Atualiza só o perfil, a senha continua a mesma
    public Usuario atualizarPerfil(int id, PerfilRequest dados) {
        Usuario atual = buscar(id);
        if (usuarioRepository.existsByDsEmailAndIdUsuarioNot(dados.dsEmail(), id))
            throw new RegraNegocioException("E-mail já cadastrado");
        atual.setNmUsuario(dados.nmUsuario());
        atual.setDsEmail(dados.dsEmail());
        atual.setNrTelefone(dados.nrTelefone());
        atual.setDtNascimento(dados.dtNascimento());
        return usuarioRepository.save(atual);
    }

    public void alterarSenha(int id, SenhaRequest dados) {
        Usuario atual = buscar(id);
        if (!atual.getHashSenha().equals(dados.senhaAtual()))
            throw new RegraNegocioException("Senha atual incorreta");
        if (dados.novaSenha().equals(dados.senhaAtual()))
            throw new RegraNegocioException("A nova senha deve ser diferente da atual");
        atual.setHashSenha(dados.novaSenha());
        usuarioRepository.save(atual);
    }

    public void deletar(int id) {
        buscar(id);
        if (contaRepository.existsByIdUsuario(id) || investimentoRepository.existsByIdUsuario(id))
            throw new RegraNegocioException("Usuário possui contas ou investimentos e não pode ser removido");
        usuarioRepository.deleteById(id);
    }
}
