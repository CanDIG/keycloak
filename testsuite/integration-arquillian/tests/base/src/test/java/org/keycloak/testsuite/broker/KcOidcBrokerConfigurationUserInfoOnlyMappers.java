package org.keycloak.testsuite.broker;

import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.IdentityProviderSyncMode;
import org.keycloak.protocol.ProtocolMapperUtils;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.mappers.HardcodedClaim;
import org.keycloak.protocol.oidc.mappers.OIDCAttributeMapperHelper;
import org.keycloak.protocol.oidc.mappers.UserAttributeMapper;
import org.keycloak.protocol.oidc.mappers.UserPropertyMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testsuite.arquillian.SuiteContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.keycloak.testsuite.broker.BrokerTestConstants.*;
import static org.keycloak.testsuite.broker.BrokerTestTools.createIdentityProvider;
import static org.keycloak.testsuite.broker.BrokerTestTools.getAuthRoot;

/**
 * @author hmlnarik
 */
public class KcOidcBrokerConfigurationUserInfoOnlyMappers extends KcOidcBrokerConfiguration {

    public static final KcOidcBrokerConfigurationUserInfoOnlyMappers INSTANCE = new KcOidcBrokerConfigurationUserInfoOnlyMappers();

    protected static final String ATTRIBUTE_TO_MAP_USER_INFO = "user-attribute-ufo";


    @Override
    public List<ClientRepresentation> createProviderClients(SuiteContext suiteContext) {
        ClientRepresentation client = new ClientRepresentation();
        client.setId(CLIENT_ID);
        client.setClientId(getIDPClientIdInProviderRealm(suiteContext));
        client.setName(CLIENT_ID);
        client.setSecret(CLIENT_SECRET);
        client.setEnabled(true);

        client.setRedirectUris(Collections.singletonList(getAuthRoot(suiteContext) +
                "/auth/realms/" + REALM_CONS_NAME + "/broker/" + IDP_OIDC_ALIAS + "/endpoint/*"));

        client.setAdminUrl(getAuthRoot(suiteContext) +
                "/auth/realms/" + REALM_CONS_NAME + "/broker/" + IDP_OIDC_ALIAS + "/endpoint");

        ProtocolMapperRepresentation userAttrMapper = new ProtocolMapperRepresentation();
        userAttrMapper.setName("attribute - name");
        userAttrMapper.setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
        userAttrMapper.setProtocolMapper(UserAttributeMapper.PROVIDER_ID);

        Map<String, String> userAttrMapperConfig = userAttrMapper.getConfig();
        userAttrMapperConfig.put(ProtocolMapperUtils.USER_ATTRIBUTE, ATTRIBUTE_TO_MAP_USER_INFO);
        userAttrMapperConfig.put(OIDCAttributeMapperHelper.TOKEN_CLAIM_NAME, ATTRIBUTE_TO_MAP_USER_INFO);
        userAttrMapperConfig.put(OIDCAttributeMapperHelper.JSON_TYPE, ProviderConfigProperty.STRING_TYPE);
        userAttrMapperConfig.put(OIDCAttributeMapperHelper.INCLUDE_IN_ACCESS_TOKEN, "false");
        userAttrMapperConfig.put(OIDCAttributeMapperHelper.INCLUDE_IN_ID_TOKEN, "false");
        userAttrMapperConfig.put(OIDCAttributeMapperHelper.INCLUDE_IN_USERINFO, "true");
        userAttrMapperConfig.put(ProtocolMapperUtils.MULTIVALUED, "true");


        client.setProtocolMappers(Arrays.asList(userAttrMapper));

        return Collections.singletonList(client);
    }

    @Override
    protected void applyDefaultConfiguration(final SuiteContext suiteContext, final Map<String, String> config, IdentityProviderSyncMode syncMode) {
        config.put(IdentityProviderModel.SYNC_MODE, syncMode.toString());
        config.put("clientId", CLIENT_ID);
        config.put("clientSecret", CLIENT_SECRET);
        config.put("prompt", "login");
        config.put("authorizationUrl", getAuthRoot(suiteContext) + "/auth/realms/" + REALM_PROV_NAME + "/protocol/openid-connect/auth");
        config.put("tokenUrl", getAuthRoot(suiteContext) + "/auth/realms/" + REALM_PROV_NAME + "/protocol/openid-connect/token");
        config.put("logoutUrl", getAuthRoot(suiteContext) + "/auth/realms/" + REALM_PROV_NAME + "/protocol/openid-connect/logout");
        config.put("userInfoUrl", getAuthRoot(suiteContext) + "/auth/realms/" + REALM_PROV_NAME + "/protocol/openid-connect/userinfo");
        config.put("defaultScope", "email profile");
        config.put("backchannelSupported", "true");
        config.put("disableUserInfo", "false");
    }


}
