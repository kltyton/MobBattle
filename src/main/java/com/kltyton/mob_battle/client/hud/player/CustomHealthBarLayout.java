package com.kltyton.mob_battle.client.hud.player;

/**
 * 自定义玩家血条的纯几何布局策略。
 *
 * <p>该类只处理 GUI logical units，不引用客户端对象，因此可以由独立
 * GameTest 验证。中央纹理装饰固定在血条局部 x=104..121，所有文字区域
 * 在此保护区两侧独立居中，避免长文本覆盖头像框。</p>
 */
public final class CustomHealthBarLayout {
    public static final int BAR_WIDTH = 224;
    public static final int BAR_HEIGHT = 32;
    public static final int BASE_Y = 12;
    public static final int PER_BOSS_OFFSET = 19;
    public static final int CUSTOM_BAR_SPACING = 38;
    public static final int PLAYER_HEAD_SIZE = 16;
    public static final int HEAD_OFFSET_X = 105;
    public static final int HEAD_OFFSET_Y = 1;
    public static final int PORTRAIT_PROTECTION_LEFT = 104;
    public static final int PORTRAIT_PROTECTION_RIGHT = 122;

    private static final int TEXT_Y_OFFSET = 12;
    private static final int TEXT_HEIGHT = 9;
    private static final int TEXT_SIDE_PADDING = 4;
    private static final int TEXT_GAP = 2;

    private CustomHealthBarLayout() {
    }

    /**
     * 根据 GUI 可用宽度和血条序号计算完整布局。
     *
     * @param guiWidth 当前 GUI logical width
     * @param bossCount 当前原版 Boss 条数量
     * @param barIndex 自定义血条序号，本地玩家为 0
     */
    public static Layout forBar(int guiWidth, int bossCount, int barIndex) {
        int barX = (guiWidth - BAR_WIDTH) / 2;
        int barY = BASE_Y + bossCount * PER_BOSS_OFFSET + barIndex * CUSTOM_BAR_SPACING;
        return new Layout(barX, barY);
    }

    /**
     * 单条血条的不可变坐标快照，所有坐标均为 GUI logical units。
     */
    public record Layout(int barX, int barY) {
        public int headX() {
            return barX + HEAD_OFFSET_X;
        }

        public int headY() {
            return barY + HEAD_OFFSET_Y;
        }

        public int textY() {
            return barY + TEXT_Y_OFFSET;
        }

        public int leftTextWidth() {
            return leftTextRight() - leftTextLeft();
        }

        public int rightTextWidth() {
            return rightTextRight() - rightTextLeft();
        }

        public int leftTextX(int textWidth) {
            return leftTextRect(textWidth).left();
        }

        public int rightTextX(int textWidth) {
            return rightTextRect(textWidth).left();
        }

        public Rect barRect() {
            return new Rect(barX, barY, barX + BAR_WIDTH, barY + BAR_HEIGHT);
        }

        /**
         * 中央保护区覆盖整条血条高度，较纹理装饰区域更严格，确保文字永不进入头像区域。
         */
        public Rect portraitRect() {
            return new Rect(
                    barX + PORTRAIT_PROTECTION_LEFT,
                    barY,
                    barX + PORTRAIT_PROTECTION_RIGHT,
                    barY + BAR_HEIGHT
            );
        }

        public Rect leftTextRect(int textWidth) {
            return centeredTextRect(leftTextLeft(), leftTextRight(), textWidth);
        }

        public Rect rightTextRect(int textWidth) {
            return centeredTextRect(rightTextLeft(), rightTextRight(), textWidth);
        }

        private int leftTextLeft() {
            return barX + TEXT_SIDE_PADDING;
        }

        private int leftTextRight() {
            return barX + PORTRAIT_PROTECTION_LEFT - TEXT_GAP;
        }

        private int rightTextLeft() {
            return barX + PORTRAIT_PROTECTION_RIGHT + TEXT_GAP;
        }

        private int rightTextRight() {
            return barX + BAR_WIDTH - TEXT_SIDE_PADDING;
        }

        private Rect centeredTextRect(int left, int right, int requestedWidth) {
            int width = Math.max(0, Math.min(requestedWidth, right - left));
            int x = left + (right - left - width) / 2;
            return new Rect(x, textY(), x + width, textY() + TEXT_HEIGHT);
        }
    }

    /**
     * 使用半开区间表示的 GUI 矩形，便于精确判断文字与中央保护区是否相交。
     */
    public record Rect(int left, int top, int right, int bottom) {
        public boolean intersects(Rect other) {
            return left < other.right
                    && right > other.left
                    && top < other.bottom
                    && bottom > other.top;
        }
    }
}
