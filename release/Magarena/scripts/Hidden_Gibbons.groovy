def PT = new MagicStatic(MagicLayer.SetPT) {
    @Override
    public void modPowerToughness(final MagicPermanent source,final MagicPermanent permanent,final MagicPowerToughness pt) {
        pt.set(4,4);
    }
};
def ST = new MagicStatic(MagicLayer.Type) {
    @Override
    public void modSubTypeFlags(final MagicPermanent permanent,final Set<MagicSubType> flags) {
        flags.add(MagicSubType.Ape);
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
            return permanent.isEnemy(cardOnStack) && permanent.isEnchantment() && cardOnStack.hasType(MagicType.Instant) ?
                new MagicEvent(
                    permanent,
                    this,
                    "SN becomes a 4/4 Ape creature."
                ):
                MagicEvent.NONE;
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            game.doAction(new MagicBecomesCreatureAction(event.getPermanent(),PT,ST));
        }
    }
]
