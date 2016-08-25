package io.dropwizard.grpc.server;

import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import io.dropwizard.validation.ValidationMethod;
import io.grpc.ServerBuilder;

/**
 * A factory for creating pre-configured {@link ServerBuilder} instances in dropwizard applications.
 *
 * The application must register gRPC services and build a gRPC server which can be lifecycle-
 * {@link ManagedGrpcServer}.
 *
 * <b>Configuration Parameters:</b>
 * <table>
 *     <tr>
 *         <td>Name</td>
 *         <td>Default</td>
 *         <td>Description</td>
 *     </tr>
 *     <tr>
 *         <td>{@code port}</td>
 *         <td>8080</td>
 *         <td>The port number where the gRPC server should listen on.</td>
 *     </tr>
 *     <tr>
 *         <td>{@code certChainFile}</td>
 *         <td>(none)</td>
 *         <td>The certificate chain file to configure transport security in the gRPC server.</td>
 *     </tr>
 *     <tr>
 *         <td>{@code privateKeyFile}</td>
 *         <td>(none)</td>
 *         <td>The private key file to configure transport security in the gRPC server.</td>
 *     </tr>
 * </table>
 */
// TODO DropwizardGrpcServerBuilder to encapsulate ServerInterceptors and ManagedGrpcServer
// TODO configure io.grpc.ServerInterceptor to collect dropwizard metrics
// TODO configure io.grpc.ServerInterceptor to send rpc call and exception events to logback
public class GrpcServerFactory {
    @Min(1)
    @Max(65535)
    private int port = 8080;

    private Path certChainFile;

    private Path privateKeyFile;

    @JsonProperty("port")
    public int getPort() {
        return port;
    }

    @JsonProperty("port")
    public void setPort(final int port) {
        this.port = port;
    }

    @JsonProperty("certChainFile")
    public Path getCertChainFile() {
        return certChainFile;
    }

    @JsonProperty("certChainFile")
    public void setCertChainFile(final Path certChainFile) {
        this.certChainFile = certChainFile;
    }

    @JsonProperty("privateKeyFile")
    public Path getPrivateKeyFile() {
        return privateKeyFile;
    }

    @JsonProperty("privateKeyFile")
    public void setPrivateKeyFile(final Path privateKeyFile) {
        this.privateKeyFile = privateKeyFile;
    }

    @ValidationMethod(message = "cert chain file {value} does not exist")
    public boolean isValidCertChainFile() {
        return certChainFile == null || Files.exists(certChainFile);
    }

    @ValidationMethod(message = "private key file {value} does not exist")
    public boolean isValidPrivateKeyFile() {
        return privateKeyFile == null || Files.exists(privateKeyFile);
    }

    /**
     * @return A {@link ServerBuilder}, with port and optional transport security set from the configuration. To use
     * this, add gRPC services to the server, call build() and then pass it to a {@link ManagedGrpcServer}.
     */
    public ServerBuilder<?> builder() {
        final ServerBuilder<?> serverBuilder;
        serverBuilder = ServerBuilder.forPort(port);
        if (certChainFile != null && privateKeyFile != null) {
            serverBuilder.useTransportSecurity(certChainFile.toFile(), privateKeyFile.toFile());
        }
        return serverBuilder;
    }
}
