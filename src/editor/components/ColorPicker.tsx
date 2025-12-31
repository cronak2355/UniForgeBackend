import { colors } from "../constants/colors";

type Props = {
    currentColor: string;
    onColorChange: (color: string) => void;
    onClose: () => void;
    anchorRect: { left: number; top: number; width: number; height: number };
};

export function ColorPicker({ currentColor, onColorChange, onClose, anchorRect }: Props) {
    return (
        <div
            style={{
                position: 'fixed',
                top: anchorRect.top,
                left: anchorRect.left + anchorRect.width / 2,
                transform: 'translate(-50%, -100%) translateY(-4px)',
                padding: '12px',
                background: colors.bgSecondary,
                border: `1px solid ${colors.borderColor}`,
                borderRadius: '8px',
                boxShadow: '0 4px 20px rgba(0,0,0,0.5)',
                zIndex: 9999,
                minWidth: '200px',
            }}
            onClick={(e) => e.stopPropagation()}
        >
            <div style={{
                display: 'flex',
                alignItems: 'center',
                gap: '12px',
            }}>
                <input
                    type="color"
                    value={currentColor}
                    onChange={(e) => onColorChange(e.target.value)}
                    style={{
                        width: '48px',
                        height: '48px',
                        border: 'none',
                        borderRadius: '6px',
                        cursor: 'pointer',
                        background: 'transparent',
                    }}
                />
                <div style={{
                    display: 'flex',
                    flexDirection: 'column',
                    gap: '4px',
                }}>
                    <span style={{
                        fontSize: '11px',
                        color: colors.textSecondary,
                        textTransform: 'uppercase',
                    }}>
                        색상 선택
                    </span>
                    <span style={{
                        fontSize: '13px',
                        color: colors.textPrimary,
                        fontFamily: 'monospace',
                    }}>
                        {currentColor.toUpperCase()}
                    </span>
                </div>
            </div>
            <button
                onClick={onClose}
                style={{
                    marginTop: '12px',
                    width: '100%',
                    padding: '8px',
                    background: colors.accentPrimary,
                    border: 'none',
                    borderRadius: '4px',
                    color: colors.textPrimary,
                    fontSize: '12px',
                    fontWeight: 500,
                    cursor: 'pointer',
                }}
            >
                확인
            </button>
        </div>
    );
}
