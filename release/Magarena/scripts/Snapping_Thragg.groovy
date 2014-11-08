[
    new MagicWhenSelfCombatDamagePlayerTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicDamage damage) {
            return new MagicEvent(
                permanent,
                new MagicMayChoice(MagicTargetChoice.TARGET_CREATURE_YOUR_OPPONENT_CONTROLS),
                new MagicDamageTargetPicker(3),
                this,
                "PN may\$ have SN deal 3 damage to target creature\$ your opponent controls."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            if (event.isYes()) {
                event.processTargetPermanent(game, {
                    final MagicDamage damage=new MagicDamage(event.getSource(),it,3);
                    game.doAction(new MagicDealDamageAction(damage));
                });
            }
        }
    }
]
