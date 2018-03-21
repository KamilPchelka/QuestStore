package artifact;

import console.menu.AbstractConsoleView;

class ArtifactStoreView extends AbstractConsoleView {

    public String artifactNameQuestion = "Enter the artifact's name: ";
    public String insufficientFunds = "Insufficient funds";
    public String productsMessage = "Artifacts by category:";
    public String artifactNotFoundError = "No such artifact found!";
    public String chooseGroup = "Choose group, or type ALONE to buy as one user: ";
    public String invalidGroupName = "The provided group name does not exist or is invalid.";
    public String magicExitString = "EXIT";
    public String abortHint = "Type " + magicExitString + " anywhere during the purchase process to abort.";

    public ArtifactStoreView() {
        // a no-menu view constructor - not all uses require a menu
    }
}