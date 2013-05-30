package org.jutge.joc.porra.entitystash.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jutge.joc.porra.entitystash.module.EntityStashEntityModule;
import org.jutge.joc.porra.entitystash.module.EntityStashViewModule;


/**
 * Entity stash annotation
 * @author Llop
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface EntityStashManaged {
	
	EntityStashEntityModule[] entities() default { EntityStashEntityModule.LEAGUES };
	
	EntityStashViewModule[] views() default { EntityStashViewModule.LEAGUE_INFO, EntityStashViewModule.LEAGUE_STATS, EntityStashViewModule.LEAGUE_POPULAR_PLAYERS, EntityStashViewModule.RICHEST_ACCOUNTS };
	
}
