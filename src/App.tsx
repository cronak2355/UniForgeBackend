import "./App.css";

function App() {
  return (
    <div className="editor-root">
      <div className="editor-topbar">
        <span>file</span>
        <span>assets</span>
        <span>edit</span>
      </div>

      <div className="editor-main">
        <div className="editor-panel left">
          <div className="editor-panel-header">Hierarchy</div>
        </div>

        <div className="editor-camera">
          <div className="editor-camera-header">Camera</div>
          <div className="editor-camera-viewport">
            {/* PhaserCanvas 삽입 */}
          </div>
        </div>

        <div className="editor-panel right">
          <div className="editor-panel-header">Inspector</div>
        </div>
      </div>

      <div className="editor-assets">
        <div className="editor-assets-tabs">
          <span>Tile</span>
          <span>Sfx</span>
        </div>
        <div className="editor-assets-grid">
          <div className="asset-item" />
          <div className="asset-item" />
          <div className="asset-item" />
        </div>
      </div>
    </div>
  );
}

export default App;
