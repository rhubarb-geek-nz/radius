// Copyright (c) 2025 Roger Brown.
// Licensed under the MIT License.

package nz.geek.rhubarb.radius.client.app;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.InetSocketAddress;
import java.util.Arrays;
import org.aaa4j.radius.client.RadiusClient;
import org.aaa4j.radius.client.RadiusClientException;
import org.aaa4j.radius.client.clients.UdpRadiusClient;
import org.aaa4j.radius.core.attribute.IntegerData;
import org.aaa4j.radius.core.attribute.StringData;
import org.aaa4j.radius.core.attribute.TextData;
import org.aaa4j.radius.core.attribute.attributes.MessageAuthenticator;
import org.aaa4j.radius.core.attribute.attributes.NasPort;
import org.aaa4j.radius.core.attribute.attributes.UserName;
import org.aaa4j.radius.core.attribute.attributes.UserPassword;
import org.aaa4j.radius.core.packet.Packet;
import org.aaa4j.radius.core.packet.packets.AccessAccept;
import org.aaa4j.radius.core.packet.packets.AccessRequest;

public class Main {

  public static void main(String[] args) throws RadiusClientException {
    int i = 0;
    String user = args[i++];
    String password = args[i++];
    String host = args[i++];
    String port = args[i++];
    String secret = args[i++];

    RadiusClient radiusClient =
        UdpRadiusClient.newBuilder()
            .secret(secret.getBytes(UTF_8))
            .address(new InetSocketAddress(host, 1812))
            .build();

    AccessRequest accessRequest =
        new AccessRequest(
            Arrays.asList(
                new MessageAuthenticator(),
                new UserName(new TextData(user)),
                new UserPassword(new StringData(password.getBytes(UTF_8))),
                new NasPort(new IntegerData(Integer.parseInt(port)))));

    Packet responsePacket = radiusClient.send(accessRequest);

    if (responsePacket instanceof AccessAccept) {
      System.out.println("Accepted");
    } else {
      System.out.println("Rejected");
    }
  }
}
