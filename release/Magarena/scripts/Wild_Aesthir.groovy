[
    new MagicPermanentActivation(
        [
            MagicCondition.ABILITY_ONCE_CONDITION,
            MagicConditionFactory.ManaCost("{W}{W}")
        ],
        new MagicActivationHints(MagicTiming.Pump),
        "Pump"
    ) {
        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [
                new MagicPayManaCostEvent(source,"{W}{W}"),
                new MagicPlayAbilityEvent(source)
            ];
        }
        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                this,
                "SN gets +2/+0 until end of turn."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            game.doAction(new MagicChangeTurnPTAction(event.getPermanent(),2,0));
        }
    }
]
