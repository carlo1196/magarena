def PT = new MagicStatic(MagicLayer.SetPT) {
    @Override
    public void modPowerToughness(final MagicPermanent source,final MagicPermanent permanent,final MagicPowerToughness pt) {
        pt.set(5,5);
    }
};
def AB = new MagicStatic(MagicLayer.Ability) {
    @Override
    public void modAbilityFlags(final MagicPermanent source,final MagicPermanent permanent,final Set<MagicAbility> flags) {
        permanent.addAbility(MagicAbility.Flying, flags);
        permanent.addAbility(MagicAbility.Vigilance, flags);
    }
};
def ST = new MagicStatic(MagicLayer.Type) {
    @Override
    public void modSubTypeFlags(final MagicPermanent permanent,final Set<MagicSubType> flags) {
        flags.add(MagicSubType.Angel);
    }
    @Override
    public int getTypeFlags(final MagicPermanent permanent,final int flags) {
        return MagicType.Creature.getMask();
    }
};
[
    new MagicWhenOtherSpellIsCastTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicCardOnStack cardOnStack) {
            return permanent.isEnemy(cardOnStack) && permanent.isEnchantment() && cardOnStack.hasType(MagicType.Creature) ?
                new MagicEvent(
                    permanent,
                    this,
                    "SN becomes a 5/5 Angel creature with flying and vigilance."
                ):
                MagicEvent.NONE;
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            game.doAction(new MagicBecomesCreatureAction(event.getPermanent(),PT,AB,ST));
        }
    }
]
