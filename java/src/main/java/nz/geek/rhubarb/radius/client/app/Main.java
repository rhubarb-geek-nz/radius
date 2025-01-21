// Copyright (c) 2025 Roger Brown.
// Licensed under the MIT License.
package nz.geek.rhubarb.radius.client.app;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.Arrays;
import org.aaa4j.radius.client.RadiusClient;
import org.aaa4j.radius.client.RadiusClientException;
import org.aaa4j.radius.client.clients.UdpRadiusClient;
import org.aaa4j.radius.core.attribute.Attribute;
import org.aaa4j.radius.core.attribute.AttributeType;
import org.aaa4j.radius.core.attribute.Data;
import org.aaa4j.radius.core.attribute.EnumData;
import org.aaa4j.radius.core.attribute.IntegerData;
import org.aaa4j.radius.core.attribute.Ipv4AddrData;
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

  public static void main(String[] args)
      throws RadiusClientException, NoSuchFieldException, IllegalArgumentException,
          IllegalAccessException {
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

    if (responsePacket != null) {
      if (responsePacket instanceof AccessAccept) {
        System.out.println("Accepted");
      } else {
        System.out.println("Rejected");
      }

      for (Attribute attr : responsePacket.getAttributes()) {
        StringBuilder sb = new StringBuilder();
        describe(attr, sb);
        System.out.println(sb.toString());
      }
    }
  }

  static void describe(Attribute attr, StringBuilder sb)
      throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
    Class c = attr.getClass();
    Field f = c.getField("NAME");
    Object name = f.get(null);
    sb.append("name=");
    sb.append(name);

    sb.append(", type=");
    describe(attr.getType(), sb);
    sb.append(", data=");
    describe(attr.getData(), sb);
  }

  static void describe(Data d, StringBuilder sb) {
    if (d instanceof TextData) {
      TextData t = (TextData) d;
      sb.append("{text=");
      sb.append(t.getValue());
      sb.append("}");
    } else {
      if (d instanceof Ipv4AddrData) {
        Ipv4AddrData t = (Ipv4AddrData) d;
        sb.append("{ipv4=");
        sb.append(t.getValue().getHostAddress());
        sb.append("}");
      } else {
        if (d instanceof EnumData) {
          EnumData t = (EnumData) d;
          sb.append("{enum=");
          sb.append(t.getValue());
          sb.append("}");
        } else {
          if (d instanceof StringData) {
            StringData t = (StringData) d;
            sb.append("{string=");
            byte[] v = t.getValue();
            for (int i = 0; i < t.length(); i++) {
              sb.append(String.format("%02x", v[i]));
            }
            sb.append("}");
          } else {
            if (d == null) {
              sb.append("null");
            } else {
              sb.append(d.toString());
            }
          }
        }
      }
    }
  }

  static void describe(AttributeType a, StringBuilder sb) {
    boolean comma = false;
    sb.append("{");
    for (int i = 0; i < a.length(); i++) {
      if (comma) {
        sb.append(":");
      }
      sb.append(a.at(i));
      comma = true;
    }
    sb.append("}");
  }
}
