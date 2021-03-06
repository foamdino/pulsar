/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.pulsar.proxy.server;

import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import org.apache.pulsar.broker.authorization.PulsarAuthorizationProvider;
import org.apache.pulsar.common.configuration.Category;
import org.apache.pulsar.common.configuration.FieldContext;
import org.apache.pulsar.common.configuration.PropertiesContext;
import org.apache.pulsar.common.configuration.PropertyContext;
import org.apache.pulsar.common.configuration.PulsarConfiguration;

@Getter
@Setter
public class ProxyConfiguration implements PulsarConfiguration {
    @Category
    private static final String CATEGORY_SERVER = "Server";
    @Category
    private static final String CATEGORY_BROKER_DISCOVERY = "Broker Discovery";
    @Category
    private static final String CATEGORY_AUTHENTICATION = "Proxy Authentication";
    @Category
    private static final String CATEGORY_AUTHORIZATION = "Proxy Authorization";
    @Category(
        description = "the settings are for configuring how proxies authenticates with Pulsar brokers"
    )
    private static final String CATEGORY_CLIENT_AUTHENTICATION = "Broker Client Authorization";
    @Category
    private static final String CATEGORY_RATE_LIMITING = "RateLimiting";
    @Category
    private static final String CATEGORY_TLS = "TLS";
    @Category
    private static final String CATEGORY_TOKEN_AUTH = "Token Authentication Provider";
    @Category
    private static final String CATEGORY_HTTP = "HTTP";

    @FieldContext(
        category = CATEGORY_BROKER_DISCOVERY,
        doc = "The ZooKeeper quorum connection string (as a comma-separated list)"
    )
    private String zookeeperServers;
    @FieldContext(
        category = CATEGORY_BROKER_DISCOVERY,
        doc = "Configuration store connection string (as a comma-separated list)"
    )
    private String configurationStoreServers;
    @FieldContext(
        category = CATEGORY_BROKER_DISCOVERY,
        doc = "Global ZooKeeper quorum connection string (as a comma-separated list)"
    )
    @Deprecated
    private String globalZookeeperServers;

    @FieldContext(
        category = CATEGORY_BROKER_DISCOVERY,
        doc = "ZooKeeper session timeout (in milliseconds)"
    )
    private int zookeeperSessionTimeoutMs = 30_000;

    @FieldContext(
        category = CATEGORY_BROKER_DISCOVERY,
        doc = "The service url points to the broker cluster"
    )
    private String brokerServiceURL;
    @FieldContext(
        category = CATEGORY_BROKER_DISCOVERY,
        doc = "The tls service url points to the broker cluster"
    )
    private String brokerServiceURLTLS;

    @FieldContext(
        category = CATEGORY_BROKER_DISCOVERY,
        doc = "The web service url points to the broker cluster"
    )
    private String brokerWebServiceURL;
    @FieldContext(
        category = CATEGORY_BROKER_DISCOVERY,
        doc = "The tls web service url points to the broker cluster"
    )
    private String brokerWebServiceURLTLS;

    @FieldContext(
        category = CATEGORY_BROKER_DISCOVERY,
        doc = "The web service url points to the function worker cluster."
            + " Only configure it when you setup function workers in a separate cluster"
    )
    private String functionWorkerWebServiceURL;
    @FieldContext(
        category = CATEGORY_BROKER_DISCOVERY,
        doc = "The tls web service url points to the function worker cluster."
            + " Only configure it when you setup function workers in a separate cluster"
    )
    private String functionWorkerWebServiceURLTLS;

    @FieldContext(
        category = CATEGORY_SERVER,
        doc = "The port for serving binary protobuf request"
    )
    private Integer servicePort = 6650;
    @FieldContext(
        category = CATEGORY_SERVER,
        doc = "The port for serving tls secured binary protobuf request"
    )
    private Integer servicePortTls;

    @FieldContext(
        category = CATEGORY_SERVER,
        doc = "The port for serving http requests"
    )
    private Integer webServicePort = 8080;
    @FieldContext(
        category = CATEGORY_SERVER,
        doc = "The port for serving https requests"
    )
    private Integer webServicePortTls;

    @FieldContext(
        category = CATEGORY_SERVER,
        doc = "Path for the file used to determine the rotation status for the proxy instance"
            + " when responding to service discovery health checks"
    )
    private String statusFilePath;

    @FieldContext(
        category = CATEGORY_AUTHORIZATION,
        doc = "A list of role names (a comma-separated list of strings) that are treated as"
            + " `super-user`, meaning they will be able to do all admin operations and publish"
            + " & consume from all topics"
    )
    private Set<String> superUserRoles = Sets.newTreeSet();

    @FieldContext(
        category = CATEGORY_AUTHENTICATION,
        doc = "Whether authentication is enabled for the Pulsar proxy"
    )
    private boolean authenticationEnabled = false;
    @FieldContext(
        category = CATEGORY_AUTHENTICATION,
        doc = "Authentication provider name list (a comma-separated list of class names"
    )
    private Set<String> authenticationProviders = Sets.newTreeSet();
    @FieldContext(
        category = CATEGORY_AUTHORIZATION,
        doc = "Whether authorization is enforced by the Pulsar proxy"
    )
    private boolean authorizationEnabled = false;
    @FieldContext(
        category = CATEGORY_AUTHORIZATION,
        doc = "Authorization provider as a fully qualified class name"
    )
    private String authorizationProvider = PulsarAuthorizationProvider.class.getName();
    @FieldContext(
        category = CATEGORY_AUTHORIZATION,
        doc = "Whether client authorization credentials are forwarded to the broker for re-authorization."
            + "Authentication must be enabled via configuring `authenticationEnabled` to be true for this"
            + "to take effect"
    )
    private boolean forwardAuthorizationCredentials = false;

    @FieldContext(
        category = CATEGORY_RATE_LIMITING,
        doc = "Max concurrent inbound connections. The proxy will reject requests beyond that"
    )
    private int maxConcurrentInboundConnections = 10000;

    @FieldContext(
        category = CATEGORY_RATE_LIMITING,
        doc = "Max concurrent lookup requests. The proxy will reject requests beyond that"
    )
    private int maxConcurrentLookupRequests = 50000;

    @FieldContext(
        category = CATEGORY_CLIENT_AUTHENTICATION,
        doc = "The authentication plugin used by the Pulsar proxy to authenticate with Pulsar brokers"
    )
    private String brokerClientAuthenticationPlugin;
    @FieldContext(
        category = CATEGORY_CLIENT_AUTHENTICATION,
        doc = "The authentication parameters used by the Pulsar proxy to authenticate with Pulsar brokers"
    )
    private String brokerClientAuthenticationParameters;
    @FieldContext(
        category = CATEGORY_CLIENT_AUTHENTICATION,
        doc = "The path to trusted certificates used by the Pulsar proxy to authenticate with Pulsar brokers"
    )
    private String brokerClientTrustCertsFilePath;

    @FieldContext(
        category = CATEGORY_CLIENT_AUTHENTICATION,
        doc = "Whether TLS is enabled when communicating with Pulsar brokers"
    )
    private boolean tlsEnabledWithBroker = false;

    /***** --- TLS --- ****/

    @Deprecated
    private boolean tlsEnabledInProxy = false;
    @FieldContext(
        category = CATEGORY_TLS,
        doc = "Tls cert refresh duration in seconds (set 0 to check on every new connection)"
    )
    private long tlsCertRefreshCheckDurationSec = 300; // 5 mins
    @FieldContext(
        category = CATEGORY_TLS,
        doc = "Path for the TLS certificate file"
    )
    private String tlsCertificateFilePath;
    @FieldContext(
        category = CATEGORY_TLS,
        doc = "Path for the TLS private key file"
    )
    private String tlsKeyFilePath;
    @FieldContext(
        category = CATEGORY_TLS,
        doc = "Path for the trusted TLS certificate file.\n\n"
            + "This cert is used to verify that any certs presented by connecting clients"
            + " are signed by a certificate authority. If this verification fails, then the"
            + " certs are untrusted and the connections are dropped"
    )
    private String tlsTrustCertsFilePath;
    @FieldContext(
        category = CATEGORY_TLS,
        doc = "Accept untrusted TLS certificate from client.\n\n"
            + "If true, a client with a cert which cannot be verified with the `tlsTrustCertsFilePath`"
            + " cert will be allowed to connect to the server, though the cert will not be used for"
            + " client authentication"
    )
    private boolean tlsAllowInsecureConnection = false;
    @FieldContext(
        category = CATEGORY_TLS,
        doc = "Whether the hostname is validated when the proxy creates a TLS connection with brokers"
    )
    private boolean tlsHostnameVerificationEnabled = false;
    @FieldContext(
        category = CATEGORY_TLS,
        doc = "Specify the tls protocols the broker will use to negotiate during TLS handshake"
            + " (a comma-separated list of protocol names).\n\n"
            + "Examples:- [TLSv1.2, TLSv1.1, TLSv1]"
    )
    private Set<String> tlsProtocols = Sets.newTreeSet();
    @FieldContext(
        category = CATEGORY_TLS,
        doc = "Specify the tls cipher the broker will use to negotiate during TLS Handshake"
            + " (a comma-separated list of ciphers).\n\n"
            + "Examples:- [TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256]"
    )
    private Set<String> tlsCiphers = Sets.newTreeSet();
    @FieldContext(
        category = CATEGORY_TLS,
        doc = "Whether client certificates are required for TLS.\n\n"
            + " Connections are rejected if the client certificate isn't trusted"
    )
    private boolean tlsRequireTrustedClientCertOnConnect = false;

    @FieldContext(
        category = CATEGORY_HTTP,
        doc = "Http directs to redirect to non-pulsar services"
    )
    private Set<HttpReverseProxyConfig> httpReverseProxyConfigs = Sets.newHashSet();

    @FieldContext(
        minValue = 1,
        category = CATEGORY_HTTP,
        doc = "Http output buffer size.\n\n"
            + "The amount of data that will be buffered for http requests "
            + "before it is flushed to the channel. A larger buffer size may "
            + "result in higher http throughput though it may take longer for "
            + "the client to see data. If using HTTP streaming via the reverse "
            + "proxy, this should be set to the minimum value, 1, so that clients "
            + "see the data as soon as possible."
    )
    private int httpOutputBufferSize = 32*1024;

    @FieldContext(
           minValue = 1,
           category = CATEGORY_HTTP,
           doc = "Number of threads to use for HTTP requests processing"
    )
    private int httpNumThreads = Math.max(4, 2 * Runtime.getRuntime().availableProcessors());

    @PropertiesContext(
        properties = {
            @PropertyContext(
                key = "tokenPublicKey",
                doc = @FieldContext(
                    category = CATEGORY_TOKEN_AUTH,
                    doc = "Asymmetric public/private key pair.\n\n"
                        + "Configure the public key to be used to validate auth tokens"
                        + " The key can be specified like:\n\n"
                        + "tokenPublicKey=data:base64,xxxxxxxxx\n"
                        + "tokenPublicKey=file:///my/public.key")
            ),
            @PropertyContext(
                key = "tokenSecretKey",
                doc = @FieldContext(
                    category = CATEGORY_TOKEN_AUTH,
                    doc = "Symmetric key.\n\n"
                        + "Configure the secret key to be used to validate auth tokens"
                        + "The key can be specified like:\n\n"
                        + "tokenSecretKey=data:base64,xxxxxxxxx\n"
                        + "tokenSecretKey=file:///my/secret.key")
            )
        }
    )
    private Properties properties = new Properties();

    public Properties getProperties() {
        return properties;
    }

    public Optional<Integer> getServicePort() {
        return Optional.ofNullable(servicePort);
    }

    public Optional<Integer> getServicePortTls() {
        return Optional.ofNullable(servicePortTls);
    }

    public Optional<Integer> getWebServicePort() {
        return Optional.ofNullable(webServicePort);
    }

    public Optional<Integer> getWebServicePortTls() {
        return Optional.ofNullable(webServicePortTls);
    }

    public void setProperties(Properties properties) {
        this.properties = properties;

        Map<String, Map<String, String>> redirects = new HashMap<>();
        Pattern redirectPattern = Pattern.compile("^httpReverseProxy\\.([^\\.]*)\\.(.+)$");
        Map<String, List<Matcher>> groups = properties.stringPropertyNames().stream()
            .map((s) -> redirectPattern.matcher(s))
            .filter(Matcher::matches)
            .collect(Collectors.groupingBy((m) -> m.group(1))); // group by name

        groups.entrySet().forEach((e) -> {
                Map<String, String> keyToFullKey = e.getValue().stream().collect(
                        Collectors.toMap(m -> m.group(2), m -> m.group(0)));
                if (!keyToFullKey.containsKey("path")) {
                    throw new IllegalArgumentException(
                            String.format("httpReverseProxy.%s.path must be specified exactly once", e.getKey()));
                }
                if (!keyToFullKey.containsKey("proxyTo")) {
                    throw new IllegalArgumentException(
                            String.format("httpReverseProxy.%s.proxyTo must be specified exactly once", e.getKey()));
                }
                httpReverseProxyConfigs.add(new HttpReverseProxyConfig(e.getKey(),
                                                    properties.getProperty(keyToFullKey.get("path")),
                                                    properties.getProperty(keyToFullKey.get("proxyTo"))));
            });
    }

    public static class HttpReverseProxyConfig {
        private final String name;
        private final String path;
        private final String proxyTo;

        HttpReverseProxyConfig(String name, String path, String proxyTo) {
            this.name = name;
            this.path = path;
            this.proxyTo = proxyTo;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        public String getProxyTo() {
            return proxyTo;
        }

        @Override
        public String toString() {
            return String.format("HttpReverseProxyConfig(%s, path=%s, proxyTo=%s)", name, path, proxyTo);
        }
    }
}