package br.com.filipecode.DeskhelpApi.tecnico.service;

import br.com.filipecode.DeskhelpApi.shared.exceptions.EntidadeNaoEncontradaException;
import br.com.filipecode.DeskhelpApi.tecnico.dto.TecnicoDTO;
import br.com.filipecode.DeskhelpApi.tecnico.dto.TecnicoRespostaDTO;
import br.com.filipecode.DeskhelpApi.tecnico.entity.Tecnico;
import br.com.filipecode.DeskhelpApi.tecnico.repository.TecnicoRepository;
import br.com.filipecode.DeskhelpApi.tecnico.validator.TecnicoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TecnicoService {

    private final TecnicoRepository tecnicoRepository;
    private final TecnicoValidator tecnicoValidator;

    public Tecnico criarTecnico(TecnicoDTO tecnicoDTO) {
        Tecnico tecnico = new Tecnico();
        tecnico.setNome(tecnicoDTO.nome());
        tecnico.setEmail(tecnicoDTO.email());
        tecnico.setEspecializacao(tecnicoDTO.especializacao());

        tecnicoValidator.validarEmailDuplicado(tecnicoDTO.email());
        return tecnicoRepository.save(tecnico);
    }

    public void atualizarTecnico(UUID id, TecnicoDTO tecnicoDTO) {
        Tecnico tecnico = tecnicoRepository.findById(id)
                        .orElseThrow(() -> new EntidadeNaoEncontradaException("Técnico não encontrado!"));

        if (!tecnico.getEmail().equals(tecnicoDTO.email())) {
            tecnicoValidator.validarEmailDuplicado(tecnicoDTO.email());
        }

        tecnico.setNome(tecnicoDTO.nome());
        tecnico.setEmail(tecnicoDTO.email());
        tecnico.setEspecializacao(tecnicoDTO.especializacao());

        tecnicoRepository.save(tecnico);

    }

    public Optional<TecnicoRespostaDTO> buscarDetalhesPorId(UUID id) {
        tecnicoValidator.validarTecnicoExiste(id);
        return tecnicoRepository.findById(id)
                .map(tecnico -> new TecnicoRespostaDTO(
                        tecnico.getId(),
                        tecnico.getNome(),
                        tecnico.getEmail(),
                        tecnico.getEspecializacao()
                ));
    }

    public List<TecnicoRespostaDTO> filtarTecnico(String especializacao) {
        List<Tecnico> tecnicos;

        if (especializacao != null) {
            tecnicos = tecnicoRepository.findByEspecializacao(especializacao);
        } else {
            tecnicos = tecnicoRepository.findAll();
        }

            return tecnicos.stream()
                    .map(tecnico -> new TecnicoRespostaDTO(
                            tecnico.getId(),
                            tecnico.getNome(),
                            tecnico.getEmail(),
                            tecnico.getEspecializacao()
                    ))
                    .collect(Collectors.toList());

    }

    public void deletarPorId(UUID id) {
        tecnicoValidator.validarTecnicoExiste(id);
        tecnicoRepository.deleteById(id);
    }
}
