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
package com.kazvoeten.tagwall.server;

import com.kazvoeten.tagwall.server.packet.ClientPacket;
import com.kazvoeten.tagwall.server.packet.PacketHandler;
import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.InPacket;
import net.Socket;
import util.HexUtils;

/**
 *
 * @author Kaz Voeten
 */
public class ClientSocket extends Socket {

    public boolean verified = false;
    public ScheduledFuture<?> verificationTask;

    public ClientSocket(Channel channel, int uSeqSend, int uSeqRcv) {
        super(channel, uSeqSend, uSeqRcv);
    }

    public void ProcessPacket(InPacket iPacket) {
        short nPacketID = iPacket.DecodeShort();
        switch (nPacketID) {
            case ClientPacket.VERIFICATION_REQUEST:
                PacketHandler.Verify(this);
                break;
            default:
                Logger.getLogger(ClientSocket.class.getName()).log(Level.INFO, "Received unhandled Client packet. nPacketID: {0}. Data: {1}", 
                        new Object[]{nPacketID, HexUtils.ToHex(iPacket.DecodeBuffer(iPacket.GetLength()))});
                break;
        }

    }

    public void verificationCheck() {
        if (!verified) {
            this.Close();
        }
    }
}
