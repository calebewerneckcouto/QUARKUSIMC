package org.acme.resources;

import org.acme.model.Pessoas;
import org.acme.repository.PessoaRepository;

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
import java.util.List;

@Path("/api/pessoas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ImcResources {

    @Inject
    PessoaRepository pessoaRepository;

    @GET
    public List<Pessoas> listarPessoas() {
        try {
            return pessoaRepository.listAll();
        } catch (Exception e) {
            // Adicione o tratamento adequado para exceções de acesso ao banco de dados
            throw new RuntimeException("Erro ao listar pessoas", e);
        }
    }

    @GET
    @Path("/buscarPorNome")
    public List<Pessoas> buscarPorNome(@QueryParam("nome") String nome) {
        try {
            // Adicione a lógica necessária para buscar pessoas pelo nome no repositório
            return pessoaRepository.buscarPorNome(nome);
        } catch (Exception e) {
            // Adicione o tratamento adequado para exceções de acesso ao banco de dados
            throw new RuntimeException("Erro ao buscar pessoas por nome", e);
        }
    }

    @POST
    @Transactional
    public Response salvarPessoa(Pessoas pessoa) {
        try {
            // Adicione validações antes de persistir a pessoa
            if (pessoa.getNome() == null || pessoa.getNome().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Nome é obrigatório").build();
            }

            pessoaRepository.persist(pessoa);
            // Retorne um código de status 201 Created após a criação bem-sucedida
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            // Adicione o tratamento adequado para exceções de acesso ao banco de dados
            throw new RuntimeException("Erro ao salvar pessoa", e);
        }
    }
}
