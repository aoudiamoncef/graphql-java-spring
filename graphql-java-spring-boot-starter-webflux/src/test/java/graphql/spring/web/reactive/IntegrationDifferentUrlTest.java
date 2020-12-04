package graphql.spring.web.reactive;

import graphql.ExecutionInput;
import graphql.ExecutionResultImpl;
import graphql.GraphQL;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "graphql.url=otherUrl")
@ExtendWith(SpringExtension.class)
public class IntegrationDifferentUrlTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    GraphQL graphql;

    @Test
    public void endpointIsAvailableWithDifferentUrl() {
        String query = "{foo}";

        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("bar")
                .build();
        CompletableFuture cf = CompletableFuture.completedFuture(executionResult);
        ArgumentCaptor<ExecutionInput> captor = ArgumentCaptor.forClass(ExecutionInput.class);
        Mockito.when(graphql.executeAsync(captor.capture())).thenReturn(cf);

        this.webClient.get().uri("/otherUrl?query={query}", query).exchange().expectStatus().isOk()
                .expectBody(String.class).isEqualTo("{\"data\":\"bar\"}");

        assertThat(captor.getValue().getQuery()).isEqualTo(query);
    }

}