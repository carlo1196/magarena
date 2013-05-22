package magic.card;

import magic.model.MagicCounterType;
import magic.model.MagicGame;
import magic.model.MagicManaCost;
import magic.model.MagicPayedCost;
import magic.model.MagicPermanent;
import magic.model.action.MagicChangeCountersAction;
import magic.model.condition.MagicCondition;
import magic.model.condition.MagicConditionFactory;
import magic.model.event.MagicActivationHints;
import magic.model.event.MagicEvent;
import magic.model.event.MagicPayManaCostEvent;
import magic.model.event.MagicPermanentActivation;
import magic.model.event.MagicRemoveCounterEvent;
import magic.model.event.MagicTiming;
import magic.model.target.MagicTargetFilter;

import java.util.Collection;

public class Carnifex_Demon {
    public static final MagicPermanentActivation A = new MagicPermanentActivation( 
            new MagicCondition[]{
                MagicCondition.MINUS_COUNTER_CONDITION,
                MagicConditionFactory.ManaCost("{B}")
            },
            new MagicActivationHints(MagicTiming.Removal),
            "-1/-1") {
        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return new MagicEvent[]{
                new MagicPayManaCostEvent(source,"{B}"),
                new MagicRemoveCounterEvent(source,MagicCounterType.MinusOne,1)};
        }
        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                    source,
                    source.getController(),
                    this,
                    "Put a -1/-1 counter on each other creature.");
        }
        @Override
        public void executeEvent(
                final MagicGame game,
                final MagicEvent event) {
            final MagicPermanent creature=event.getPermanent();
            final Collection<MagicPermanent> targets=
                game.filterPermanents(creature.getController(),MagicTargetFilter.TARGET_CREATURE);
            for (final MagicPermanent target : targets) {
                if (target!=creature) {
                    game.doAction(new MagicChangeCountersAction(target,MagicCounterType.MinusOne,1,true));
                }
            }
        }
    };
}
