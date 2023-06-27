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

package com.github.juacom99.rcon4j.net.common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 *
 * @author juacom99
 */
public class RCONPackage
{
    private int id;
    private RCONPackageType type;
    private String payload;    
     
    private static final Charset CHARSET=Charset.forName("utf-8");

    public RCONPackage(int id, RCONPackageType type, String payload)
    {
        this.id = id;
        this.type = type;
        this.payload = payload;
    }
    
    public int getSize()
    {
        return CHARSET.encode(this.payload).limit()+10; 
    }

    public String getPayload()
    {
        return payload;
    }

    public int getId()
    {
        return id;
    }

    public RCONPackageType getType()
    {
        return type;
    }
    
    public ByteBuffer serialize()
    {
        int pkgSize=getSize();
        ByteBuffer pkg=ByteBuffer.allocate(pkgSize+4);
        pkg.order(ByteOrder.LITTLE_ENDIAN);        
        pkg.putInt(pkgSize);
        pkg.putInt(id);
        pkg.putInt(type.value);
        pkg.put(CHARSET.encode(this.payload));
        pkg.put((byte)0);
        pkg.put((byte)0);
        pkg.flip();
        return pkg;
    }
    
    public static RCONPackage unserialize(int size, ByteBuffer pkgBuffer,boolean fromServer)
    {
           
        pkgBuffer.order(ByteOrder.LITTLE_ENDIAN);
        pkgBuffer.position(0);
        int id=pkgBuffer.getInt();
        RCONPackageType type=RCONPackageType.valueOf(pkgBuffer.getInt(),fromServer);
        
        String payload="";
        if(size>10)
        {
            ByteBuffer pPayload= pkgBuffer.slice(pkgBuffer.position(),size-10);
            pkgBuffer.position(pkgBuffer.position()+size-10);
            payload=Charset.forName("utf-8").decode(pPayload).toString();
        }
        
        byte stringNullTermination=pkgBuffer.get();
        byte packageNullTermination=pkgBuffer.get();
        
        
        return new RCONPackage(id, type, payload);
    }

    @Override
    public String toString()
    {
        return "SIZE: "+getSize()+"\nID: "+this.id+"\nTYPE:"+this.type+"\nPAYLOAD:"+this.payload;
    }
    
    
    
}
