package net.lyflow.entitytest.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerLook;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.control.EntityAIBodyControl;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.entity.monster.IMonster;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class Ship2 extends EntityFlying implements IMonster {

    public static final float b = 7.448451F;
    public static final int c = MathHelper.f(24.166098F);
    private static final DataWatcherObject<Integer> d;

    static {
        d = DataWatcher.a(Ship2.class, DataWatcherRegistry.b);
    }

    Vec3D e;
    BlockPosition bS;
    Ship2.AttackPhase bT;

    public Ship2(EntityTypes<? extends EntityFlying> entitytypes, World world) {
        super(entitytypes, world);
        this.e = Vec3D.b;
        this.bS = BlockPosition.b;
        this.bT = Ship2.AttackPhase.a;

        this.bJ = 5;
        this.bL = new Ship2.g(this);
        this.bK = new Ship2.f(this);
    }

    public boolean aT() {
        return (this.w() + this.ah) % c == 0;
    }

    protected EntityAIBodyControl H() {
        return new Ship2.d(this);
    }

    protected void B() {
        this.bO.a(1, new Ship2.c());
        this.bO.a(2, new Ship2.i());
        this.bO.a(3, new Ship2.e());
        this.bP.a(1, new Ship2.b());
    }

    protected void c_() {
        super.c_();
        this.an.a(d, 0);
    }

    public void b(int i) {
        this.an.b(d, MathHelper.a(i, 0, 64));
    }

    private void A() {
        this.k_();
        this.a(GenericAttributes.c).a(6 + this.u());
    }

    public int u() {
        return this.an.b(d);
    }

    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.b * 0.35F;
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (d.equals(datawatcherobject)) {
            this.A();
        }

        super.a(datawatcherobject);
    }

    public int w() {
        return this.aj() * 3;
    }

    protected boolean X() {
        return true;
    }

    public void l() {
        super.l();
        if (this.dM().B) {
            float f = MathHelper.b((float) (this.w() + this.ah) * 7.448451F * 0.017453292F + 3.1415927F);
            float f1 = MathHelper.b((float) (this.w() + this.ah + 1) * 7.448451F * 0.017453292F + 3.1415927F);
            if (f > 0.0F && f1 <= 0.0F) {
                this.dM().a(this.dr(), this.dt(), this.dx(), SoundEffects.sA, this.db(), 0.95F + this.ag.i() * 0.05F, 0.95F + this.ag.i() * 0.05F, false);
            }

            int i = this.u();
            float f2 = MathHelper.b(this.dC() * 0.017453292F) * (1.3F + 0.21F * (float) i);
            float f3 = MathHelper.a(this.dC() * 0.017453292F) * (1.3F + 0.21F * (float) i);
            float f4 = (0.3F + f * 0.45F) * ((float) i * 0.2F + 1.0F);
            this.dM().a(Particles.W, this.dr() + (double) f2, this.dt() + (double) f4, this.dx() + (double) f3, 0.0, 0.0, 0.0);
            this.dM().a(Particles.W, this.dr() - (double) f2, this.dt() + (double) f4, this.dx() - (double) f3, 0.0, 0.0, 0.0);
        }

    }

    protected void Z() {
        super.Z();
    }

    public GroupDataEntity a(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.bS = this.dm().b(5);
        this.b(0);
        return super.a(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.e("AX")) {
            this.bS = new BlockPosition(nbttagcompound.h("AX"), nbttagcompound.h("AY"), nbttagcompound.h("AZ"));
        }

        this.b(nbttagcompound.h("Size"));
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.a("AX", this.bS.u());
        nbttagcompound.a("AY", this.bS.v());
        nbttagcompound.a("AZ", this.bS.w());
        nbttagcompound.a("Size", this.u());
    }

    public boolean a(double d0) {
        return true;
    }

    public SoundCategory db() {
        return SoundCategory.f;
    }

    protected SoundEffect y() {
        return SoundEffects.sx;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.sB;
    }

    protected SoundEffect n_() {
        return SoundEffects.sz;
    }

    public EnumMonsterType eS() {
        return EnumMonsterType.b;
    }

    protected float eW() {
        return 1.0F;
    }

    public boolean a(EntityTypes<?> entitytypes) {
        return true;
    }

    public EntitySize a(EntityPose entitypose) {
        int i = this.u();
        EntitySize entitysize = super.a(entitypose);
        return entitysize.a(1.0F + 0.15F * (float) i);
    }

    protected Vector3f a(Entity entity, EntitySize entitysize, float f) {
        return new Vector3f(0.0F, entitysize.b * 0.675F, 0.0F);
    }

    protected float l(Entity entity) {
        return -0.125F;
    }

    private enum AttackPhase {
        a,
        b;

        AttackPhase() {
        }
    }

    private class b extends PathfinderGoal {
        private final PathfinderTargetCondition b = PathfinderTargetCondition.a().a(64.0);
        private int c = b(20);

        b() {
        }


        public boolean a() {
            if (this.c > 0) {
                --this.c;
                return false;
            } else {
                this.c = b(60);
                List<EntityHuman> list = Ship2.this.dM().a(this.b, Ship2.this, Ship2.this.cH().c(16.0, 64.0, 16.0));
                if (!list.isEmpty()) {
                    list.sort(Comparator.comparing((Object e) -> {
                        try {
                            return HeightMap.Type.e;
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }).reversed());
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        EntityHuman entityhuman = (EntityHuman) iterator.next();
                        if (Ship2.this.a(entityhuman, PathfinderTargetCondition.a)) {
                            Ship2.this.setTarget(entityhuman, TargetReason.CLOSEST_PLAYER, true);
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        public boolean b() {
            EntityLiving entityliving = Ship2.this.q();
            return entityliving != null && Ship2.this.a(entityliving, PathfinderTargetCondition.a);
        }
    }

    private class c extends PathfinderGoal {
        private int b;

        c() {
        }

        public boolean a() {
            EntityLiving entityliving = Ship2.this.q();
            return entityliving != null && Ship2.this.a(entityliving, PathfinderTargetCondition.a);
        }

        public void c() {
            this.b = this.a(10);
            Ship2.this.bT = Ship2.AttackPhase.a;
            this.h();
        }

        public void d() {
            Ship2.this.bS = Ship2.this.dM().a(HeightMap.Type.e, Ship2.this.bS).b(10 + Ship2.this.ag.a(20));
        }

        public void e() {
            if (Ship2.this.bT == Ship2.AttackPhase.a) {
                --this.b;
                if (this.b <= 0) {
                    Ship2.this.bT = Ship2.AttackPhase.b;
                    this.h();
                    this.b = this.a((8 + Ship2.this.ag.a(4)) * 20);
                    Ship2.this.a(SoundEffects.sC, 10.0F, 0.95F + Ship2.this.ag.i() * 0.1F);
                }
            }

        }

        private void h() {
            Ship2.this.bS = Ship2.this.q().dm().b(20 + Ship2.this.ag.a(20));
            if (Ship2.this.bS.v() < Ship2.this.dM().A_()) {
                Ship2.this.bS = new BlockPosition(Ship2.this.bS.u(), Ship2.this.dM().A_() + 1, Ship2.this.bS.w());
            }

        }
    }

    private class d extends EntityAIBodyControl {
        public d(EntityInsentient entityinsentient) {
            super(entityinsentient);
        }

        public void a() {
            Ship2.this.aW = Ship2.this.aU;
            Ship2.this.aU = Ship2.this.dC();
        }
    }

    private class e extends Ship2.h {
        private float c;
        private float d;
        private float e;
        private float f;

        e() {
            super();
        }

        public boolean a() {
            return Ship2.this.q() == null || Ship2.this.bT == Ship2.AttackPhase.a;
        }

        public void c() {
            this.d = 5.0F + Ship2.this.ag.i() * 10.0F;
            this.e = -4.0F + Ship2.this.ag.i() * 9.0F;
            this.f = Ship2.this.ag.h() ? 1.0F : -1.0F;
            this.i();
        }

        public void e() {
            if (Ship2.this.ag.a(this.a(350)) == 0) {
                this.e = -4.0F + Ship2.this.ag.i() * 9.0F;
            }

            if (Ship2.this.ag.a(this.a(250)) == 0) {
                ++this.d;
                if (this.d > 15.0F) {
                    this.d = 5.0F;
                    this.f = -this.f;
                }
            }

            if (Ship2.this.ag.a(this.a(450)) == 0) {
                this.c = Ship2.this.ag.i() * 2.0F * 3.1415927F;
                this.i();
            }

            if (this.h()) {
                this.i();
            }

            if (Ship2.this.e.d < Ship2.this.dt() && !Ship2.this.dM().u(Ship2.this.dm().c(1))) {
                this.e = Math.max(1.0F, this.e);
                this.i();
            }

            if (Ship2.this.e.d > Ship2.this.dt() && !Ship2.this.dM().u(Ship2.this.dm().b(1))) {
                this.e = Math.min(-1.0F, this.e);
                this.i();
            }

        }

        private void i() {
            if (BlockPosition.b.equals(Ship2.this.bS)) {
                Ship2.this.bS = Ship2.this.dm();
            }

            this.c += this.f * 15.0F * 0.017453292F;
            Ship2.this.e = Vec3D.a(Ship2.this.bS).b(this.d * MathHelper.b(this.c), -4.0F + this.e, this.d * MathHelper.a(this.c));
        }
    }

    private class f extends ControllerLook {
        public f(EntityInsentient entityinsentient) {
            super(entityinsentient);
        }

        public void a() {
        }
    }

    private class g extends ControllerMove {
        private float m = 0.1F;

        public g(EntityInsentient entityinsentient) {
            super(entityinsentient);
        }

        public void a() {
            if (Ship2.this.P) {
                Ship2.this.r(Ship2.this.dC() + 180.0F);
                this.m = 0.1F;
            }

            double d0 = Ship2.this.e.c - Ship2.this.dr();
            double d1 = Ship2.this.e.d - Ship2.this.dt();
            double d2 = Ship2.this.e.e - Ship2.this.dx();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            if (Math.abs(d3) > 9.999999747378752E-6) {
                double d4 = 1.0 - Math.abs(d1 * 0.699999988079071) / d3;
                d0 *= d4;
                d2 *= d4;
                d3 = Math.sqrt(d0 * d0 + d2 * d2);
                double d5 = Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);
                float f = Ship2.this.dC();
                float f1 = (float) MathHelper.d(d2, d0);
                float f2 = MathHelper.g(Ship2.this.dC() + 90.0F);
                float f3 = MathHelper.g(f1 * 57.295776F);
                Ship2.this.r(MathHelper.e(f2, f3, 4.0F) - 90.0F);
                Ship2.this.aU = Ship2.this.dC();
                if (MathHelper.d(f, Ship2.this.dC()) < 3.0F) {
                    this.m = MathHelper.d(this.m, 1.8F, 0.005F * (1.8F / this.m));
                } else {
                    this.m = MathHelper.d(this.m, 0.2F, 0.025F);
                }

                float f4 = (float) (-(MathHelper.d(-d1, d3) * 57.2957763671875));
                Ship2.this.s(f4);
                float f5 = Ship2.this.dC() + 90.0F;
                double d6 = (double) (this.m * MathHelper.b(f5 * 0.017453292F)) * Math.abs(d0 / d5);
                double d7 = (double) (this.m * MathHelper.a(f5 * 0.017453292F)) * Math.abs(d2 / d5);
                double d8 = (double) (this.m * MathHelper.a(f4 * 0.017453292F)) * Math.abs(d1 / d5);
                Vec3D vec3d = Ship2.this.dp();
                Ship2.this.g(vec3d.e((new Vec3D(d6, d8, d7)).d(vec3d).a(0.2)));
            }

        }
    }

    private abstract class h extends PathfinderGoal {
        public h() {
            this.a(EnumSet.of(net.minecraft.world.entity.ai.goal.PathfinderGoal.Type.a));
        }

        protected boolean h() {
            return Ship2.this.e.c(Ship2.this.dr(), Ship2.this.dt(), Ship2.this.dx()) < 4.0;
        }
    }

    private class i extends Ship2.h {
        private static final int c = 20;
        private boolean d;
        private int e;

        i() {
            super();
        }

        public boolean a() {
            return Ship2.this.q() != null && Ship2.this.bT == Ship2.AttackPhase.b;
        }

        public boolean b() {
            EntityLiving entityliving = Ship2.this.q();
            if (entityliving == null) {
                return false;
            } else if (!entityliving.bx()) {
                return false;
            } else {
                if (entityliving instanceof EntityHuman entityhuman) {
                    if (entityliving.P_() || entityhuman.f()) {
                        return false;
                    }
                }

                if (!this.a()) {
                    return false;
                } else {
                    if (Ship2.this.ah > this.e) {
                        this.e = Ship2.this.ah + 20;
                        List<EntityCat> list = Ship2.this.dM().a(EntityCat.class, Ship2.this.cH().g(16.0), IEntitySelector.a);
                        Iterator iterator = list.iterator();

                        while (iterator.hasNext()) {
                            EntityCat entitycat = (EntityCat) iterator.next();
                            entitycat.gp();
                        }

                        this.d = !list.isEmpty();
                    }

                    return !this.d;
                }
            }
        }

        public void c() {
        }

        public void d() {
            Ship2.this.h((EntityLiving) null);
            Ship2.this.bT = Ship2.AttackPhase.a;
        }

        public void e() {
            EntityLiving entityliving = Ship2.this.q();
            if (entityliving != null) {
                Ship2.this.e = new Vec3D(entityliving.dr(), entityliving.e(0.5), entityliving.dx());
                if (Ship2.this.cH().g(0.20000000298023224).c(entityliving.cH())) {
                    Ship2.this.C(entityliving);
                    Ship2.this.bT = Ship2.AttackPhase.a;
                    if (!Ship2.this.aU()) {
                        Ship2.this.dM().c(1039, Ship2.this.dm(), 0);
                    }
                } else if (Ship2.this.P || Ship2.this.aK > 0) {
                    Ship2.this.bT = Ship2.AttackPhase.a;
                }
            }

        }
    }
}
