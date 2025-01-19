// Copyright (c) 2025 Roger Brown.
// Licensed under the MIT License.

using Flexinets.Radius.Core;
using Flexinets.Radius;
using Microsoft.Extensions.Logging;
using System.Net;

int i = 0;
String user = args[i++];
String password = args[i++];
String host = args[i++];
String port = args[i++];
String secret = args[i++];

var loggerFactory = LoggerFactory.Create(builder =>
{
    builder.AddFilter("Microsoft", LogLevel.Warning)
           .AddFilter("System", LogLevel.Warning)
           .AddFilter("SampleApp.Program", LogLevel.Debug)
           .AddConsole();
});

using var client = new RadiusClient(
    new IPEndPoint(IPAddress.Any, 58733),
    new RadiusPacketParser(
        loggerFactory.CreateLogger<RadiusPacketParser>(),
        RadiusDictionary.Parse(DefaultDictionary.RadiusDictionary)));

var requestPacket = new RadiusPacket(PacketCode.AccessRequest, 0, secret);
requestPacket.AddMessageAuthenticator();
requestPacket.AddAttribute("User-Name", user);
requestPacket.AddAttribute("User-Password", password);
requestPacket.AddAttribute("NAS-Port", port);

var responsePacket = await client.SendPacketAsync(
    requestPacket,
    new IPEndPoint(IPAddress.Parse(host), 1812));

Console.WriteLine($"{responsePacket.Code}");
