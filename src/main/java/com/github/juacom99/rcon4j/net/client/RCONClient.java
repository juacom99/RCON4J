/*
 * Copyright (C) 2023 Joaquin Martinez <juacom04@gmail.com>
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

package com.github.juacom99.rcon4j.net.client;

import com.github.juacom99.rcon4j.exception.RCONInvalidAuthenticationException;
import com.github.juacom99.rcon4j.net.common.RCONPackage;
import com.github.juacom99.rcon4j.net.common.RCONPackageType;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.IIOException;

/**
 *
 * @author juacom99
 */
public class RCONClient
{
    private SocketChannel client;
    private AtomicInteger currentId;
    private ByteBuffer readBuffer;
    
    private final int READ_BUFFER_SIZE=1024*4; //4k

    public RCONClient() throws IOException
    {
        client=SocketChannel.open();
        currentId=new AtomicInteger(0);
        readBuffer=ByteBuffer.allocate(READ_BUFFER_SIZE);
    }
    
    
    public void connect(InetSocketAddress serverAddr,String password) throws IOException, RCONInvalidAuthenticationException
    {
        if(!client.isConnected() && !client.isConnectionPending())
        {
            client.connect(serverAddr);
            RCONPackage authPkg=new RCONPackage(currentId.addAndGet(1), RCONPackageType.SERVERDATA_AUTH, password);
            RCONPackage authResp=send(authPkg);
            
            if(authPkg.getId()==-1 || authPkg.getId()!=authResp.getId())
            {
                //Authentication failed
                throw new RCONInvalidAuthenticationException("Invalid password");
            }
        }
    }
    
    
    public String exec(String command) throws IOException
    {
        RCONPackage commandPkg=new RCONPackage(currentId.addAndGet(1), RCONPackageType.SERVERDATA_EXECCOMMAND, command);
        RCONPackage commandResponse=send(commandPkg);        
        return commandResponse.getPayload();
    }
    
    private RCONPackage send(RCONPackage pkg) throws IOException
    {
        if(client.isConnected() && client.isOpen())
        {
            client.write(pkg.serialize());           
            readBuffer.order(ByteOrder.LITTLE_ENDIAN);
            client.read(readBuffer);
            readBuffer.position(0);
            int responseSize=readBuffer.getInt();
            byte[] pkgData=new byte[responseSize];
            readBuffer.get(pkgData);                        
            readBuffer.clear();
       
            
            return RCONPackage.unserialize(responseSize,ByteBuffer.wrap(pkgData),true);
        }
        else
        {
            throw new IIOException("Unable to send data, Socket is closed");
        }
    }
    
}
