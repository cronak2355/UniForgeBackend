import { useState } from "react";
import type { Asset } from "./types/Asset"
type Props = {
  onChangeValue: (selectedAsset: Asset) => void;
  assets: Asset[];
};

export function AssetPanel({ onChangeValue, assets }: Props) {
  const [currentTag, setCurrentTag] = useState<string>("Tile");

  return (
    <>
      <div className="editor-assets-tabs">
        <span onClick={() => setCurrentTag("Tile")}>Tile</span>
        <span onClick={() => setCurrentTag("Character")}>Character</span>
      </div>
      <div className="editor-assets-grid">
        {Object.values(assets)
          .filter(asset => asset.tag == currentTag)
          .map(asset => (
            <img src={asset.url} key={asset.id} className="asset-item" onClick={ () => {
              onChangeValue(asset)
            } } />
          ))}
      </div>
    </>
  );
}
