[
    new MagicPermanentActivation(
        [MagicCondition.HAS_CARD_CONDITION],
        new MagicActivationHints(MagicTiming.Pump,false,1),
        "Shadow"
    ) {
        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [
                new MagicDiscardEvent(source,source.getController(),1,false),
                new MagicPlayAbilityEvent(source)
            ];
        }
        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                this,
                "SN gains shadow until end of turn."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            game.doAction(new MagicSetAbilityAction(event.getPermanent(),MagicAbility.Shadow));
        }
    }
]
