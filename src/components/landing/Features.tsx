import { memo, useRef } from 'react';

const Features = () => {
    const sectionRef = useRef<HTMLElement>(null);

    const handleMouseMove = (e: React.MouseEvent<HTMLElement>) => {
        if (!sectionRef.current) return;

        const rect = sectionRef.current.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;

        sectionRef.current.style.setProperty('--mouse-x', `${x}px`);
        sectionRef.current.style.setProperty('--mouse-y', `${y}px`);
    };

    return (
        <section
            id="features"
            className="features"
            ref={sectionRef}
            onMouseMove={handleMouseMove}
        >
            <div className="ribbon-container">
                <svg className="ribbon-svg" viewBox="0 0 1440 320" preserveAspectRatio="none">
                    <path
                        className="ribbon-path-bg"
                        d="M0,96L48,112C96,128,192,160,288,160C384,160,480,128,576,112C672,96,768,96,864,112C960,128,1056,160,1152,160C1248,160,1344,128,1392,112L1440,96V320H1392C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320H0V96Z"
                    />
                    <path
                        className="ribbon-path"
                        d="M0,160 C320,300, 420,0, 740,160 C1060,320, 1120,0, 1440,160"
                        fill="none"
                        strokeWidth="4"
                    />
                    <path
                        className="ribbon-path-2"
                        d="M0,160 C320,20, 420,320, 740,160 C1060,0, 1120,320, 1440,160"
                        fill="none"
                        strokeWidth="4"
                    />
                </svg>
            </div>

            <div className="container" style={{ position: 'relative', zIndex: 2 }}>
                <div className="section-header reveal">
                    <h2>왜 <span className="gradient-text">Uniforge</span>인가요?</h2>
                    <p>게임 개발이 처음인 당신을 위한 완벽한 도구입니다.</p>
                </div>
                <div className="features-grid">
                    <div className="feature-card reveal delay-100">
                        <div className="icon-box">
                            <i className="fa-solid fa-shapes"></i>
                        </div>
                        <h3>풍부한 에셋 라이브러리</h3>
                        <p>수천 개의 캐릭터, 아이템, 배경 에셋을 무료로 사용하세요. 클릭만 하면 내 게임 속에 들어옵니다.</p>
                    </div>
                    <div className="feature-card reveal delay-200">
                        <div className="icon-box">
                            <i className="fa-solid fa-bolt"></i>
                        </div>
                        <h3>직관적인 에디터</h3>
                        <p>파워포인트보다 쉽습니다. 마우스 드래그로 맵을 꾸미고 이벤트를 설정해보세요.</p>
                    </div>
                    <div className="feature-card reveal delay-300">
                        <div className="icon-box">
                            <i className="fa-solid fa-wand-magic-sparkles"></i>
                        </div>
                        <h3>AI 어시스턴트</h3>
                        <p>"보스 몬스터 만들어줘"라고 말만 하세요. AI가 당신의 기획을 현실로 만들어줍니다.</p>
                    </div>
                </div>
            </div>
        </section>
    );
};

export default memo(Features);
