with import <nixpkgs> {};
mkShell {
    buildInputs = [
       eclipses.eclipse-jee
       jdk8
       maven
    ];
}
