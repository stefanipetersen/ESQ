package br.com.fiap.esq.service;

import br.com.fiap.esq.exception.EntidadeNaoEncontradaException;
import br.com.fiap.esq.exception.RegraNegocioException;
import br.com.fiap.esq.model.Usuario;
import br.com.fiap.esq.repository.ContaRepository;
import br.com.fiap.esq.repository.InvestimentoRepository;
import br.com.fiap.esq.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HexFormat;
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

    public Usuario cadastrar(Usuario usuario) {
        if (usuarioRepository.existsByDsEmail(usuario.getDsEmail()))
            throw new RegraNegocioException("E-mail já cadastrado");
        usuario.setIdUsuario(0);
        usuario.setDtCadastro(LocalDate.now());
        usuario.setHashSenha(gerarHash(usuario.getHashSenha()));
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizar(int id, Usuario dados) {
        Usuario atual = buscar(id);
        if (usuarioRepository.existsByDsEmailAndIdUsuarioNot(dados.getDsEmail(), id))
            throw new RegraNegocioException("E-mail já cadastrado");
        atual.setNmUsuario(dados.getNmUsuario());
        atual.setDsEmail(dados.getDsEmail());
        atual.setHashSenha(gerarHash(dados.getHashSenha()));
        return usuarioRepository.save(atual);
    }

    public void deletar(int id) {
        buscar(id);
        if (contaRepository.existsByIdUsuario(id) || investimentoRepository.existsByIdUsuario(id))
            throw new RegraNegocioException("Usuário possui contas ou investimentos e não pode ser removido");
        usuarioRepository.deleteById(id);
    }

    private String gerarHash(String senha) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(senha.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
