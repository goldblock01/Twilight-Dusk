package github.gold_block.mixin;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import java.util.List;
import java.util.Set;

/** 检查可选的箭矢注入是否接入目标方法，并报告缺失的功能。 */
public class ArrowMixinAuditPlugin implements IMixinConfigPlugin {
    /** 使用配置文件指定的静态 Mixin 清单。 */
    @Override
    public void onLoad(String mixinPackage) {
    }

    /** 使用配置文件中的默认映射表。 */
    @Override
    public String getRefMapperConfig() {
        return null;
    }

    /** 允许配置中的 Mixin 参与转换。 */
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    /** 保留转换器提供的目标类集合。 */
    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    /** 不添加配置文件以外的 Mixin。 */
    @Override
    public List<String> getMixins() {
        return null;
    }

    /** 保留注入前的类结构。 */
    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    /** 在转换结果中查找箭矢处理调用，对未接入的可选功能输出提示。 */
    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        String suffix;
        if (mixinClassName.endsWith("KnightArrowArmorMixin") || mixinClassName.endsWith("KnightArrowPlayerArmorMixin")) {
            suffix = "$armorPenetration";
        } else if (mixinClassName.endsWith("KnightArrowEnchantmentMixin")) {
            suffix = "$enchantmentPenetration";
        } else if (mixinClassName.endsWith("PhantomArrowGravityMixin")) {
            suffix = "$halfGravity";
        } else {
            return;
        }
        for (MethodNode method : targetClass.methods) {
            for (AbstractInsnNode instruction : method.instructions) {
                if (instruction instanceof MethodInsnNode call && call.name.endsWith(suffix)) {
                    return;
                }
            }
        }
        System.err.println("twilight_dusk: optional arrow hook missing: " + mixinClassName
                + " in " + targetClassName + "; this feature is inactive for the target.");
    }
}
