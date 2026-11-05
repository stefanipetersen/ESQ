package br.com.fiap.esq.service;

import br.com.fiap.esq.exception.EntidadeNaoEncontradaException;
import br.com.fiap.esq.exception.RegraNegocioException;
import br.com.fiap.esq.model.Configuracao;
import br.com.fiap.esq.repository.ConfiguracaoRepository;
import br.com.fiap.esq.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ConfiguracaoService {

    private static final Set<String> MOEDAS = Set.of("BRL", "USD", "EUR");
    private static final Set<String> FORMATOS_DATA = Set.of("DD/MM/AAAA", "MM/DD/AAAA", "AAAA-MM-DD");

    private final ConfiguracaoRepository configuracaoRepository;
    private final UsuarioRepository usuarioRepository;

    public ConfiguracaoService(ConfiguracaoRepository configuracaoRepository, UsuarioRepository usuarioRepository) {
        this.configuracaoRepository = configuracaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Configuracao> listar(Integer idUsuario) {
        return idUsuario == null ? configuracaoRepository.findAll() : configuracaoRepository.findByIdUsuario(idUsuario);
    }

    public Configuracao buscar(int id) {
        return configuracaoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Configuração não encontrada"));
    }

    public Configuracao cadastrar(Configuracao configuracao) {
        validar(configuracao);
        if (configuracaoRepository.existsByIdUsuario(configuracao.getIdUsuario()))
            throw new RegraNegocioException("O usuário já possui configurações");
        configuracao.setIdConfiguracao(0);
        return configuracaoRepository.save(configuracao);
    }

    public Configuracao atualizar(int id, Configuracao dados) {
        Configuracao atual = buscar(id);
        validar(dados);
        if (configuracaoRepository.existsByIdUsuarioAndIdConfiguracaoNot(dados.getIdUsuario(), id))
            throw new RegraNegocioException("O usuário já possui configurações");
        atual.setCdMoeda(dados.getCdMoeda());
        atual.setCdFormatoData(dados.getCdFormatoData());
        atual.setFlTemaClaro(dados.isFlTemaClaro());
        atual.setFlResumoEmail(dados.isFlResumoEmail());
        atual.setFlDicas(dados.isFlDicas());
        atual.setIdUsuario(dados.getIdUsuario());
        return configuracaoRepository.save(atual);
    }

    public void deletar(int id) {
        buscar(id);
        configuracaoRepository.deleteById(id);
    }

    private void validar(Configuracao c) {
        if (!MOEDAS.contains(c.getCdMoeda()))
            throw new RegraNegocioException("Moeda deve ser BRL, USD ou EUR");
        if (!FORMATOS_DATA.contains(c.getCdFormatoData()))
            throw new RegraNegocioException("Formato de data deve ser DD/MM/AAAA, MM/DD/AAAA ou AAAA-MM-DD");
        if (!usuarioRepository.existsById(c.getIdUsuario()))
            throw new RegraNegocioException("Usuário informado não existe");
    }
}
