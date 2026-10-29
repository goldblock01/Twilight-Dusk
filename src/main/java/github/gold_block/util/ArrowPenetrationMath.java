package github.gold_block.util;

/** 按实际减伤量计算箭矢穿透后的阶段伤害。 */
public final class ArrowPenetrationMath {
    private ArrowPenetrationMath() {
    }

    /** 返还指定比例的有效减伤；无效输入保留原阶段结果。 */
    public static float restore(float before, float after, double fraction) {
        if (!Float.isFinite(before) || !Float.isFinite(after) || !Double.isFinite(fraction)
                || before <= 0.0F || after < 0.0F || after >= before) {
            return after;
        }
        double ratio = Math.max(0.0D, Math.min(1.0D, fraction));
        double result = after + ((double) before - after) * ratio;
        return Double.isFinite(result) && result <= Float.MAX_VALUE ? (float) result : after;
    }
}
