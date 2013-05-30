package org.jutge.joc.porra.entitystash.aspect;

import java.util.List;
import java.util.Locale;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jutge.joc.porra.entitystash.annotation.EntityStashManaged;
import org.jutge.joc.porra.entitystash.module.EntityStashEntityModule;
import org.jutge.joc.porra.entitystash.module.EntityStashViewModule;
import org.jutge.joc.porra.entitystash.service.EntityStashEntityService;
import org.jutge.joc.porra.entitystash.service.EntityStashViewService;
import org.jutge.joc.porra.entitystash.stash.EntityStash;
import org.jutge.joc.porra.entitystash.utils.EntityStashUtils;
import org.jutge.joc.porra.model.account.Account;
import org.jutge.joc.porra.model.league.League;
import org.jutge.joc.porra.model.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


/**
 * Entity stash aspect.
 * Can query DB and stash data to minimize app-DB interaction.
 * Can inject additional request params. These are json strings.  
 * @author Llop
 */
@Aspect
public class EntityStashAspect {


	public static final String DEFAULT_LEAGUE_NAME = "FIB";
	public static final String DEFAULT_LEAGUE_INFO = "defaultLeagueInfo";
	public static final String DEFAULT_LEAGUE_STATS = "defaultLeagueStats";
	public static final String DEFAULT_LEAGUE_POPULAR_PLAYERS = "defaultLeaguePopularPlayers";
	public static final String DEFAULT_LEAGUE_ALIVE_PLAYERS = "defaultLeaguePlayers";
	public static final String DEFAULT_LEAGUE_BET_RESTRICTIONS = "defaultLeagueBetRestrictions";
	public static final String ACCOUNT_BET_RESTRICTIONS = "accountBetRestrictions";
	public static final String ACCOUNT_INFO = "accountInfo";
	public static final String ACCOUNT_BETS = "accountBets";
	public static final String RICHEST_ACCOUNTS = "richestAccounts";
	

	private final Logger logger = LoggerFactory.getLogger(getClass());


	@Autowired private EntityStashEntityService entityStashEntityService;
	@Autowired private EntityStashViewService entityStashViewService;
	
	
	@Before(value="within(org.jutge.joc.porra.controller..*) && @annotation(entityStashManaged)")
	public void stashEntities(final JoinPoint joinPoint, final EntityStashManaged entityStashManaged) {
		this.logger.info("EntityStashAspect.stashEntities");
		final EntityStash entityStash = this.getEntityStash(joinPoint);
		this.applyEntityModules(entityStash, entityStashManaged);
		final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		requestAttributes.setAttribute(EntityStashUtils.ENTITY_STASH, entityStash, RequestAttributes.SCOPE_REQUEST);
	}
	
	private EntityStash getEntityStash(final JoinPoint joinPoint) {
		final Object[] args = joinPoint.getArgs();
		for (final Object obj : args) {
			if (obj instanceof EntityStash) {
				return (EntityStash)obj;
			}
		}
		return new EntityStash();
	}
	
	private void applyEntityModules(final EntityStash entityStash, final EntityStashManaged entityStashManaged) {
		List<League> leagues = null;
		List<Player> players = null;
		Account account = null;
		final EntityStashEntityModule[] entities = entityStashManaged.entities();
		for (EntityStashEntityModule entity : entities) {
			if (entity == EntityStashEntityModule.LEAGUES || entity == EntityStashEntityModule.ALL) {
				leagues = this.entityStashEntityService.getLeagues();
			}
			if (entity == EntityStashEntityModule.PLAYERS || entity == EntityStashEntityModule.ALL) {
				players = this.entityStashEntityService.getPlayers();
			}
			if (entity == EntityStashEntityModule.ACCOUNT || entity == EntityStashEntityModule.ALL) {
				account = this.entityStashEntityService.getAccount();
			}
		}
		entityStash.init(leagues, players, account);
	}
	
	@After("within(org.jutge.joc.porra.controller..*) && @annotation(entityStashManaged)")
	public void stashViews(final EntityStashManaged entityStashManaged) {
		this.logger.info("EntityStashAspect.stashViews");
		final Locale locale = LocaleContextHolder.getLocale();
		final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		final EntityStash entityStash = (EntityStash)requestAttributes.getAttribute(EntityStashUtils.ENTITY_STASH, RequestAttributes.SCOPE_REQUEST);
		final EntityStashViewModule[] views = entityStashManaged.views();
		for (EntityStashViewModule view : views) {
			try {
				if (view == EntityStashViewModule.LEAGUE_INFO || view == EntityStashViewModule.ALL) {
					final League defaultLeague = entityStash.getLeague(DEFAULT_LEAGUE_NAME);
					final String leagueInfo = this.entityStashViewService.getLeagueInfo(defaultLeague, locale);
					requestAttributes.setAttribute(DEFAULT_LEAGUE_INFO, leagueInfo, RequestAttributes.SCOPE_REQUEST);
				}
				if (view == EntityStashViewModule.LEAGUE_STATS || view == EntityStashViewModule.ALL) {
					final League defaultLeague = entityStash.getLeague(DEFAULT_LEAGUE_NAME);
					final String leagueStats = this.entityStashViewService.getLeagueStats(defaultLeague, locale);
					requestAttributes.setAttribute(DEFAULT_LEAGUE_STATS, leagueStats, RequestAttributes.SCOPE_REQUEST);
				}
				if (view == EntityStashViewModule.LEAGUE_POPULAR_PLAYERS || view == EntityStashViewModule.ALL) {
					final League defaultLeague = entityStash.getLeague(DEFAULT_LEAGUE_NAME);
					final String leaguePopularPlayers = this.entityStashViewService.getLeaguePopularPlayers(defaultLeague, locale);
					requestAttributes.setAttribute(DEFAULT_LEAGUE_POPULAR_PLAYERS, leaguePopularPlayers, RequestAttributes.SCOPE_REQUEST);
				}
				if (view == EntityStashViewModule.LEAGUE_ALIVE_PLAYERS || view == EntityStashViewModule.ALL) {
					final League defaultLeague = entityStash.getLeague(DEFAULT_LEAGUE_NAME);
					final List<Player> leaguePlayers = entityStash.getLeaguePlayers(defaultLeague);
					final String alivePlayers = this.entityStashViewService.getAlivePlayers(leaguePlayers);
					requestAttributes.setAttribute(DEFAULT_LEAGUE_ALIVE_PLAYERS, alivePlayers, RequestAttributes.SCOPE_REQUEST);
				}
				if (view == EntityStashViewModule.LEAGUE_BET_RESTRICTIONS || view == EntityStashViewModule.ALL) {
					final League defaultLeague = entityStash.getLeague(DEFAULT_LEAGUE_NAME);
					final String leagueBetRestrictions = this.entityStashViewService.getLeagueBetRestrictions(defaultLeague, locale);
					requestAttributes.setAttribute(DEFAULT_LEAGUE_BET_RESTRICTIONS, leagueBetRestrictions, RequestAttributes.SCOPE_REQUEST);
				} 
				if (view == EntityStashViewModule.ACCOUNT_BET_RESTRICTIONS || view == EntityStashViewModule.ALL) {
					final Account account = entityStash.getAccount();
					final String accountBetRestrictions = this.entityStashViewService.getAccountBetRestrictions(account, locale);
					requestAttributes.setAttribute(ACCOUNT_BET_RESTRICTIONS, accountBetRestrictions, RequestAttributes.SCOPE_REQUEST);
				}
				if (view == EntityStashViewModule.RICHEST_ACCOUNTS || view == EntityStashViewModule.ALL) {
					final String richestAccounts = this.entityStashViewService.getRichestAccounts(entityStash, locale);
					requestAttributes.setAttribute(RICHEST_ACCOUNTS, richestAccounts, RequestAttributes.SCOPE_REQUEST);
				}
				if (view == EntityStashViewModule.ACCOUNT_INFO || view == EntityStashViewModule.ALL) {
					final Account account = entityStash.getAccount();
					if (account != null) {
						final String accountInfo = this.entityStashViewService.getAccountInfo(account, locale);
						requestAttributes.setAttribute(ACCOUNT_INFO, accountInfo, RequestAttributes.SCOPE_REQUEST);
					}
				} 
				if (view == EntityStashViewModule.ACCOUNT_BETS || view == EntityStashViewModule.ALL) {
					final Account account = entityStash.getAccount();
					if (account != null) {
						final String accountBets = this.entityStashViewService.getAccountBets(account, entityStash, locale);
						requestAttributes.setAttribute(ACCOUNT_BETS, accountBets, RequestAttributes.SCOPE_REQUEST);
					}
				}
			} catch (Exception exception) {
				this.logger.error(exception.getMessage());
			}
		}
	}
	
	
}
