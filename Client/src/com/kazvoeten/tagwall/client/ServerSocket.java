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
import com.kazvoeten.tagwall.client.packet.ServerPacket;
import io.netty.channel.Channel;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.InPacket;
import net.Socket;
import net.SocketMode;
import util.HexUtils;

/**
 *
 * @author Kaz Voeten
 */
public class ServerSocket extends Socket {

    public boolean verified = false;

    public ServerSocket(Channel channel, int uSeqSend, int uSeqRcv) {
        super(SocketMode.SERVER, channel, uSeqSend, uSeqRcv);
    }

    public void ProcessPacket(InPacket iPacket) {
        try {
            short nPacketID = iPacket.DecodeShort();
            switch (nPacketID) {
                case ServerPacket.VERIFICATION_RESPONSE:
                    PacketHandler.Verify(this, iPacket);
                    break;
                default:
                    Logger.getLogger(ServerSocket.class.getName()).log(Level.INFO, "Received unhandled Client packet. nPacketID: {0}. Data: {1}",
                            new Object[]{nPacketID, HexUtils.ToHex(iPacket.DecodeBuffer(iPacket.GetLength()))});

                    break;
            }
        } catch (Exception ex) {
            Logger.getLogger(ServerSocket.class.getName()).log(Level.SEVERE, "An unknown exception has occured: ", ex);
        }
    }
}
