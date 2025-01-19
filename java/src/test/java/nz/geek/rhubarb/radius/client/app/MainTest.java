// Copyright (c) 2025 Roger Brown.
// Licensed under the MIT License.

package nz.geek.rhubarb.radius.client.app;

import org.aaa4j.radius.client.RadiusClientException;
import org.junit.Assert;
import org.junit.Test;

/** @author rogerb */
public class MainTest {

  /** Test of main method, of class Main. */
  @Test
  public void testMain() throws RadiusClientException {
    String[] args = new String[] {"bob", "test", "127.0.0.1", "0", "testing123"};
    Main.main(args);
    Assert.assertTrue(true);
  }
}
