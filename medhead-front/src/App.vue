<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-6 text-center">
        
        <img src="./assets/logo.png" alt="Logo MedHead" class="logo-medhead mb-3">
        
        <!-- Affichage des erreurs global -->
        <div v-if="error" class="alert alert-danger">
          {{ error }}
        </div>
        
        <!-- Écran de connexion -->
        <div v-if="!isAuthenticated" class="card shadow-sm mb-4">
          <div class="card-header bg-primary text-white fw-bold">
            Accès - Opérateur d'urgence
          </div>
          <div class="card-body text-start">
            <div class="mb-3">
              <label class="form-label">Identifiant</label>
              <input v-model="usernameInput" type="text" class="form-control" />
            </div>
            <div class="mb-4">
              <label class="form-label">Mot de passe</label>
              <input v-model="passwordInput" type="password" class="form-control" />
            </div>
            <button @click="login" class="btn btn-primary w-100 fw-bold">
              Se connecter
            </button>
          </div>
        </div>

        <!-- Écran de recherche -->
        <div v-else>
          <h2 class="mb-4">Recherche d'Hôpital</h2>
          
          <div class="card shadow-sm mb-4">
            <div class="card-body">
              
              <div class="mb-3 text-start">
                <label class="form-label fw-bold">Spécialité requise</label>
                <select v-model="specialism" class="form-select">
                  <optgroup label="Médecine d'urgence">
                    <option value="Médecine d'urgence">Médecine d'urgence</option>
                  </optgroup>
                  <optgroup label="Groupe de médecine générale">
                    <option value="Cardiologie">Cardiologie</option>
                    <option value="Neurologie">Neurologie</option>
                    <option value="Médecine respiratoire">Médecine respiratoire</option>
                  </optgroup>
                  <!-- Autres spécialités à ajouter ici -->
                </select>
              </div>

              <!-- Bloc de localisation simulée -->
              <div class="mb-4 text-start">
                <label class="form-label fw-bold">Lieu de l'intervention</label>
                <select v-model="selectedLocation" class="form-select">
                  <option :value="{lat: 51.503, lon: -0.114}">Gare de Waterloo, Londres</option>
                  <option :value="{lat: 51.507, lon: -0.165}">Hyde Park, Londres</option>
                  <option :value="{lat: 51.523, lon: -0.073}">Quartier Shoreditch, Londres</option>
                  <option :value="{lat: 51.493, lon: -0.144}">Gare Victoria, Londres</option>
                </select>
              </div>

              <button @click="searchHospital" class="btn btn-primary w-100 fw-bold">
                Trouver l'hôpital le plus proche
              </button>
              
            </div>
          </div>

          <!-- Affichage des résultats -->
          <div v-if="hospital" class="card result-card text-start border-success">
            <div class="card-body">
              <h4 class="card-title text-success">Intervention affectée</h4>
              <p class="card-text mb-1"><strong>Établissement :</strong> {{ hospital.name }}</p>
              <p class="card-text"><strong>Lits disponibles :</strong> <span class="badge bg-success">{{ hospital.availableBeds }}</span></p>
            </div>
          </div>

          <!-- Bouton de déconnexion -->
          <div class="mt-3 text-end">
             <button @click="logout" class="btn btn-sm btn-outline-secondary">Se déconnecter</button>
          </div>

        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

// États d'authentification
const isAuthenticated = ref(false)
const usernameInput = ref('')
const passwordInput = ref('')
const error = ref('')

// États de recherche
const specialism = ref('Cardiologie')
const selectedLocation = ref({ lat: 51.503, lon: -0.114 })
const hospital = ref(null)

// Fonctions
const login = () => {
  // Vérification locale de la saisie
  if (usernameInput.value && passwordInput.value) {
    isAuthenticated.value = true
    error.value = ''
  } else {
    error.value = "Veuillez saisir vos identifiants."
  }
}

const logout = () => {
  isAuthenticated.value = false
  usernameInput.value = ''
  passwordInput.value = ''
  hospital.value = null
  error.value = ''
}

const searchHospital = async () => {
  try {
    error.value = ''
    hospital.value = null
    
    const response = await axios.get('http://localhost:8080/api/hospitals/nearest', {
      params: { 
        specialism: specialism.value, 
        lat: selectedLocation.value.lat, 
        lon: selectedLocation.value.lon 
      },
      // Injection des identifiants pour Basic Auth
      auth: { 
        username: usernameInput.value, 
        password: passwordInput.value 
      }
    })
    
    hospital.value = response.data
  } catch (err) {
    if (err.response && err.response.status === 401) {
      error.value = "Erreur d'authentification : Identifiants invalides."
      isAuthenticated.value = false // Forcer la déconnexion si l'API rejette les credentials
    } else if (err.response && err.response.status === 404) {
      error.value = "Aucun hôpital trouvé avec des lits disponibles dans ce périmètre."
    } else {
      error.value = "Erreur de connexion au serveur MedHead."
    }
  }
}
</script>