package org.acme.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acme.model.Pessoas;
import org.acme.repository.PessoaRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/pessoas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ImcResources {

    @Inject
    PessoaRepository pessoaRepository;

    // Inject an instance of your message service here
    @Inject
    MessageService messageService;

    @GET
    public List<Pessoas> listarPessoas() {
        try {
            return pessoaRepository.listAll();
        } catch (Exception e) {
            // Adicione o tratamento adequado para exceções de acesso ao banco de dados
            messageService.sendMessage("Erro ao listar pessoas", "error");
            throw new RuntimeException("Erro ao listar pessoas", e);
        }
    }
    
    @GET
    @Path("/buscarPorNome")
    public Response buscarPorNome(@QueryParam("nome") String nome) {
        try {
            List<Pessoas> pessoas = pessoaRepository.buscarPorNome(nome);

            if (pessoas.isEmpty()) {
                messageService.sendMessage("Nenhuma pessoa encontrada com o nome fornecido.", "info");
                return Response.status(Response.Status.NOT_FOUND).entity("Nenhuma pessoa encontrada com o nome fornecido.").build();
            }

            List<Pessoas> dadosGrafico = pessoaRepository.buscarPorNome(nome);

            if (dadosGrafico.isEmpty()) {
                messageService.sendMessage("Nenhum dado disponível para o gráfico.", "info");
                return Response.status(Response.Status.NOT_FOUND).entity("Nenhum dado disponível para o gráfico.").build();
            }

            // Chame a função para criar o gráfico com os dados obtidos e retorne o JSON do gráfico
            String jsonData = criarGrafico(dadosGrafico);

            return Response.ok(jsonData).build();
        } catch (Exception e) {
            // Adicione o tratamento adequado para exceções de acesso ao banco de dados
            messageService.sendMessage("Erro ao buscar pessoas por nome", "error");
            throw new RuntimeException("Erro ao buscar pessoas por nome", e);
        }
    }

    // ...

    private String criarGrafico(List<Pessoas> dadosGrafico) {
        List<Map<String, Object>> chartData = new ArrayList<>();

        for (Pessoas dados : dadosGrafico) {
            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("id", dados.getId());
            dataPoint.put("data", dados.getData());
            dataPoint.put("nome", dados.getNome()); // Incluí o campo "nome" no JSON
            dataPoint.put("peso", dados.getPeso());
            dataPoint.put("altura", dados.getAltura());
            dataPoint.put("idade", dados.getIdade());
            dataPoint.put("resultaImc", dados.getResultaImc());
            chartData.add(dataPoint);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(chartData);
        } catch (JsonProcessingException e) {
            messageService.sendMessage("Erro ao converter dados do gráfico para JSON", "error");
            throw new RuntimeException("Erro ao converter dados do gráfico para JSON", e);
        }
    }

    @POST
    @Transactional
    public Response salvarPessoa(Pessoas pessoa) {
        try {
            // Adicione validações antes de persistir a pessoa
            if (pessoa.getNome() == null || pessoa.getNome().trim().isEmpty()) {
                messageService.sendMessage("Nome é obrigatório", "error");
                return Response.status(Response.Status.BAD_REQUEST).entity("Nome é obrigatório").build();
            }

            pessoaRepository.persist(pessoa);
            // Retorne um código de status 201 Created após a criação bem-sucedida
            messageService.sendMessage("Pessoa salva com sucesso.", "success");
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            // Adicione o tratamento adequado para exceções de acesso ao banco de dados
            messageService.sendMessage("Erro ao salvar pessoa", "error");
            throw new RuntimeException("Erro ao salvar pessoa", e);
        }
    }
}
