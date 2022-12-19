with import <nixpkgs> {};
mkShell {
    buildInputs = [
       eclipses.eclipse-jee
       jdk11
       maven
    ];
}
