/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package tachyon.security;

import java.security.Provider;
import java.util.Map;

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslServerFactory;

/**
 * The Java SunSASL provider supports CRAM-MD5, DIGEST-MD5 and GSSAPI mechanism for server side.
 * When the SASL using PLAIN mechanism, there is no support the SASL server.
 * So there is a new provider needed to register to support server-side PLAIN mechanism.
 */
public class PlainSaslServerProvider extends Provider {
  public static final String PROVIDER = "SaslPlain";
  public static final String MECHANISM = "PLAIN";

  public PlainSaslServerProvider() {
    super(PROVIDER, 1.0, "Plain SASL provider");
    put("SaslServerFactory." + MECHANISM, PlainSaslServerFactory.class.getName());
  }

  /**
   * PlainSaslServerFactory is used to create instances of {@link PlainSaslServer}.
   * The parameter mechanism must be "PLAIN" when this PlainSaslServerFactory is called, or
   * the null will be return.
   */
  public static class PlainSaslServerFactory implements SaslServerFactory {
    /**
     * Creates a SaslServer using the parameters supplied.
     * It returns null if no SaslServer can be created using the parameters supplied.
     * Throws SaslException if it cannot create a SaslServer
     * because of an error.
     * @param mechanism The name of a SASL mechanism. (e.g. "PLAIN").
     * @param protocol The non-null string name of the protocol for which
     * the authentication is being performed.
     * @param serverName The non-null fully qualified host name of the server
     * to authenticate to.
     * @param props The possibly null set of properties used to select the SASL
     * mechanism and to configure the authentication exchange of the selected
     * mechanism.
     * @param cbh The possibly null callback handler to used by the SASL
     * mechanisms to do further operation.
     *@return A possibly null SaslServer created using the parameters
     * supplied. If null, this factory cannot produce a SaslServer
     * using the parameters supplied.
     *@exception SaslException If cannot create a SaslServer because of an error.
     */
    @Override
    public SaslServer createSaslServer(String mechanism, String protocol, String serverName,
        Map<String, ?> props, CallbackHandler cbh) throws SaslException {
      if (MECHANISM.equals(mechanism)) {
        return new PlainSaslServer(cbh);
      }
      return null;
    }

    @Override
    public String[] getMechanismNames(Map<String, ?> props) {
      return new String[] {MECHANISM};
    }
  }
}
