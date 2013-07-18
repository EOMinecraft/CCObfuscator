package codechicken.obfuscator;

import org.objectweb.asm.commons.Remapper;

import codechicken.lib.asm.ObfMapping;
import codechicken.obfuscator.ObfuscationMap.ObfuscationEntry;

public class ObfRemapper extends Remapper
{
    public final ObfuscationMap obf;
    public ObfDirection dir;
    
    public ObfRemapper(ObfuscationMap obf, ObfDirection dir)
    {
        this.obf = obf;
        this.dir = dir;
    }
    
    @Override
    public String map(String name)
    {
        ObfuscationEntry map;
        if(dir.obfuscate)
            map = obf.lookupMcpClass(name);
        else
            map = obf.lookupObfClass(name);

        if(map != null)
            return dir.obfuscate(map).s_owner;
        
        return name;
    }
    
    @Override
    public String mapFieldName(String owner, String name, String desc)
    {
        ObfuscationEntry map;
        if(dir.obfuscate)
            map = obf.lookupMcpField(owner, name);
        else
            map = obf.lookupObfField(owner, name);
        
        if(map == null)
            map = obf.lookupSrg(name);
        
        if(map != null)
            return dir.obfuscate(map).s_name;
        
        return name;
    }
    
    @Override
    public String mapMethodName(String owner, String name, String desc)
    {
        if(owner.charAt(0) == '[')
            return name;
        
        ObfuscationEntry map;
        if(dir.obfuscate)
            map = obf.lookupMcpMethod(owner, name, desc);
        else
            map = obf.lookupObfMethod(owner, name, desc);
        
        if(map == null)
            map = obf.lookupSrg(name);
        
        if(map != null)
            return dir.obfuscate(map).s_name;
        
        return name;
    }

    public void map(ObfMapping map)
    {
        if(map.s_desc.contains("("))
        {
            map.s_name = mapMethodName(map.s_owner, map.s_name, map.s_desc);
            map.s_desc = mapMethodDesc(map.s_desc);
        }
        else
        {
            map.s_name = mapFieldName(map.s_owner, map.s_name, map.s_desc);
            map.s_desc = mapDesc(map.s_desc);
        }
        map.s_owner = map(map.s_owner);
    }
    
    @Override
    public Object mapValue(Object cst)
    {
        if(!(cst instanceof String))
            return cst;

        if(dir.srg_cst)
        {
            ObfuscationEntry map = obf.lookupSrg((String) cst);
            if(map != null)
                return dir.obfuscate(map).s_name;
        }
        return cst;
    }
}
