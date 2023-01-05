package org.marketcetera.tensorflow;

import org.marketcetera.module.ModuleManager;
import org.marketcetera.symbol.IterativeSymbolResolver;
import org.marketcetera.symbol.PatternSymbolResolver;
import org.marketcetera.symbol.SymbolResolverService;
import org.marketcetera.tensorflow.converters.TensorFromEventConverter;
import org.marketcetera.tensorflow.converters.TensorFromOrderConverter;
import org.marketcetera.tensorflow.converters.TensorFromReportConverter;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootConfiguration
@EnableAutoConfiguration
public class TensorFlowTestConfiguration
{
    @Bean
    public TensorFromOrderConverter getTensorFromOrderConverter()
    {
        return new TensorFromOrderConverter();
    }
    @Bean
    public TensorFromEventConverter getTensorFromEventConverter()
    {
        return new TensorFromEventConverter();
    }
    @Bean
    public TensorFromReportConverter getTensorFromReportConverter()
    {
        return new TensorFromReportConverter();
    }
    @Bean
    public TensorFromJpegConverter getTensorFromJpegConverter()
    {
        return new TensorFromJpegConverter();
    }
    /**
     * Get the symbol resolver service value.
     *
     * @return a <code>SymbolResolverService</code> value
     */
    @Bean
    public SymbolResolverService getSymbolResolverService()
    {
        IterativeSymbolResolver symbolResolverService = new IterativeSymbolResolver();
        symbolResolverService.getSymbolResolvers().add(new PatternSymbolResolver());
        return symbolResolverService;
    }
    /**
     * Get the module manager value.
     *
     * @return a <code>ModuleManager</code> value
     */
    @Bean
    public ModuleManager getModuleManager()
    {
        ModuleManager moduleManager = ModuleManager.getInstance();
        if(moduleManager == null) {
            moduleManager = new ModuleManager();
            moduleManager.init();
        }
        System.out.println("COCO: returning test ModuleManager bean");
        return moduleManager;
    }
}
