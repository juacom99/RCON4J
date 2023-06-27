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

package com.github.juacom99.rcon4j.net.server;

import com.github.juacom99.rcon4j.net.common.RCONPackage;
import com.github.juacom99.rcon4j.net.common.RCONPackageType;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author jomartinez
 */
public class RCONServer
{
    private Selector selector;
    private ServerSocketChannel server;
    private String password;
    private boolean running;
    private ByteBuffer readBuffer;
    
    private final int READ_BUFFER_SIZE=1024*4;
    
    

    public RCONServer(String password) throws IOException
    {
        selector=Selector.open();
        server=ServerSocketChannel.open();
        this.password=password;
        this.readBuffer=ByteBuffer.allocate(READ_BUFFER_SIZE);
        this.readBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }
    
    
    public void start(InetSocketAddress serverAddr) throws IOException
    {
        server.bind(serverAddr);
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);
        running=true;
        int pkgSize;
        byte[] pkgData;
        int id;
        RCONPackage pkg,resp = null;
        System.out.println("Server started on "+serverAddr.getHostString()+":"+serverAddr.getPort());
        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();
            while (iter.hasNext()) {

                SelectionKey key = iter.next();

                if (key.isAcceptable()) {
                    SocketChannel client = server.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                    System.out.println(client.getRemoteAddress()+" connected");
                }

                if (key.isReadable())
                {
                    SocketChannel client = (SocketChannel) key.channel();
                    client.read(readBuffer);
                    readBuffer.flip();
                    pkgSize=readBuffer.getInt();
                    pkgData=new byte[pkgSize];
                    readBuffer.get(pkgData);
                    pkg=RCONPackage.unserialize(pkgSize, ByteBuffer.wrap(pkgData),false);
                    
                    System.out.println(" ************************ LLegó ************************");
                    System.out.println(pkg);
                    
                    switch (pkg.getType())
                    {
                        case SERVERDATA_AUTH ->
                        {
                            if(pkg.getPayload().equals(password))
                            {
                                id=pkg.getId();
                            }
                            else
                            {
                                id=-1;
                            }
                            
                            resp=new RCONPackage(id, RCONPackageType.SERVERDATA_AUTH_RESPONSE,"");
                        }
                        case SERVERDATA_EXECCOMMAND -> 
                        {
                            resp=new RCONPackage(pkg.getId(), RCONPackageType.SERVERDATA_RESPONSE_VALUE,"Command response here");                        
                        }
                    }
                    
                    if(resp!=null)
                    {
                         client.write(resp.serialize());
                        
                        System.out.println(" ************************ Rerspondí ************************");
                        System.out.println(resp);
                    }
                    readBuffer.clear();
                    
                }
                iter.remove();
            }
        }
    }
    
}
