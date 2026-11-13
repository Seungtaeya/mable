package demo.mable.admin.enums;

public enum AssetType {
    CHARACTER("characters"),
    CHARACTER_SKILL("characterSkill"),
    DICE("dice"),
    BUILDING("building"),
    LUCKY_ITEM("lucky-item");

    private final String directory;

    AssetType(String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }
}
