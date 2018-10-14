/*
 * Copyright (C) 2018 Kaz Voeten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kazvoeten.tagwall.client;

import com.kazvoeten.tagwall.client.packet.PacketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.InPacket;

/**
 *
 * @author Kaz Voeten
 */
public class SocketHandler extends ChannelInboundHandlerAdapter {

    public static ServerSocket socket;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();

        ServerSocket client = new ServerSocket(channel, 0, 0);
        channel.attr(ServerSocket.SESSION_KEY).set(client);
        socket = client;

        client.SendPacket(PacketHandler.verificationRequest());
        Logger.getLogger(SocketHandler.class.getName()).log(Level.INFO, "Connected to server.");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();

        ServerSocket client = (ServerSocket) channel.attr(ServerSocket.SESSION_KEY).get();
        client.Close();

        socket = null;
        Logger.getLogger(SocketHandler.class.getName()).log(Level.INFO, "Connection to server was terminated.");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object in) {
        Channel channel = ctx.channel();
        ServerSocket client = (ServerSocket) channel.attr(ServerSocket.SESSION_KEY).get();
        InPacket iPacket = (InPacket) in;
        client.ProcessPacket(iPacket);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
        Channel channel = ctx.channel();
        Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, "An unknown exception has occured.");
        ServerSocket client = (ServerSocket) channel.attr(ServerSocket.SESSION_KEY).get();
        if (client != null) {
            socket = null;
            client.Close();
        }
    }
}
