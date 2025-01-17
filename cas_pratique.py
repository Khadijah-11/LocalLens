class Personne():
    def __init__(self, nom, prenom):
        self.nom = nom
        self.prenom = prenom

    def __str__(self):
        return f"{self.nom} {self.prenom}"

class Cours:
    def __init__(self,titre, code):
        self.titre = titre 
        self.code = code
        self.etudiants = []

    def ajouter_etudiant(self, etudiants):
        self.etudiants.append(etudiants)
    def __str__(self):
        return f"{self.titre}"

        
class Etudiant(Personne):
    def __init__(self, nom, prenom, cne):
        super().__init__(nom, prenom)
        self.cne = cne
        self.mes_cours = []
    def inscrit_cours(self, cours):
        self.mes_cours.append(cours)
    def afficher_cours(self):
        return [cours.titre for cours in self.mes_cours]
    def __str__(self):
        return f"L'etudiant {self.nom} {self.prenom} portant le CNE {self.cne}"
    
class Professeur(Personne):
    def __init__(self, nom, prenom, cin):
        super().__init__(nom, prenom)
        self.cin = cin 
        self.cours_enseigne = []

    def ajouter_cours(self, cours):
        self.cours_enseigne.append(cours)


big_data = Cours("Big Data", "BD")
python = Cours("Analyse de données avec Python", "ADP")

prof = Professeur("Prof.DLIOU", "Azzedine", "DD1234")
prof.ajouter_cours(python)

prof_1 = Professeur("Prof.LAMARI", "Yasmine", "EE4321")
prof_1.ajouter_cours(big_data)

etudiant = Etudiant("Lasfer", "Nisrine", "67809")
etudiant.inscrit_cours(python)
etudiant.inscrit_cours(big_data)

python.ajouter_etudiant(etudiant)

print(f"l'etudiant {etudiant} est inscrit au cours {etudiant.afficher_cours()} enseigné par les professeurs {prof_1} et {prof}")