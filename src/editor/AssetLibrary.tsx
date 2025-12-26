import { useState, useEffect } from "react";
import type { Asset } from "../data/Asset"
export function AssetLibrary() {
  // assets: <string, Asset>
  const [assets, setAssets] = useState<Record<string, Asset>>({});

  // 현재 선택된 태그
  const [currentTag, setCurrentTag] = useState<"Tile" | "Sfx">("Tile");

  return (
    <>
      <div className="editor-assets-tabs">
        <span onClick={() => setCurrentTag("Tile")}>Tile</span>
        <span onClick={() => setCurrentTag("Sfx")}>Sfx</span>
      </div>

      <div className="editor-assets-grid">
        {Object.values(assets)
          .filter(asset => asset.tag === currentTag)
          .map(asset => (
            <div key={asset.id} className="asset-item" />
          ))}
      </div>
    </>
  );
}
