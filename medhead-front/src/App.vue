<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-8 text-center">
        
        <img src="./assets/logo.png" alt="Logo MedHead" class="logo-medhead mb-4" style="max-width: 200px;">
        <!-- Affichage des erreurs global -->
        <div v-if="error" class="alert alert-danger shadow-sm">
          {{ error }}
        </div>
        
        <!-- Écran de connexion -->
        <div v-if="!isAuthenticated" class="card shadow border-0 mb-4">
          <div class="card-header bg-primary text-white fw-bold p-3">
            Accès - Opérateur d'urgence
          </div>
          <div class="card-body p-4 text-start">
            <div class="mb-3">
              <label class="form-label fw-bold">Identifiant</label>
              <input v-model="usernameInput" type="text" class="form-control" />
            </div>
            <div class="mb-4">
              <label class="form-label fw-bold">Mot de passe</label>
              <input v-model="passwordInput" type="password" class="form-control" />
            </div>
            <button @click="login" class="btn btn-primary w-100 fw-bold p-2">
              Se connecter
            </button>
          </div>
        </div>

        <!-- Écran de recherche -->
        <div v-else>
          <h2 class="mb-4">Système d'Affectation d'Urgence</h2>
          
          <div class="card shadow border-0 mb-4">
            <div class="card-body p-4">
              
              <div class="row">
                <div class="col-md-6 mb-3 text-start">
                  <label class="form-label fw-bold">Spécialité NHS requise</label>
                  <select v-model="specialism" class="form-select">
                    <optgroup label="Médecine d'urgence">
                  <option value="Médecine d'urgence">Médecine d'urgence</option>
                </optgroup>
                <optgroup label="Groupe de médecine générale">
                  <option value="Cardiologie">Cardiologie</option>
                  <option value="Neurologie">Neurologie</option>
                  <option value="Médecine respiratoire">Médecine respiratoire</option>
                </optgroup>

                <optgroup label="Groupe chirurgical">
                  <option value="Chirurgie générale">Chirurgie générale</option>
                  <option value="Chirurgie cardiothoracique">Chirurgie cardiothoracique</option>
                  <option value="Traumatologie et chirurgie orthopédique">Traumatologie et chirurgie orthopédique</option>
                </optgroup>

                <optgroup label="Groupe dentaire">
                  <option value="Chirurgie buccale et maxillo-faciale">Chirurgie buccale et maxillo-faciale</option>
                  <option value="Orthodontie">Orthodontie</option>
                </optgroup>

                <optgroup label="Groupe de pathologie">
                  <option value="Immunologie">Immunologie</option>
                  <option value="Microbiologie médicale">Microbiologie médicale</option>
                  <option value="Virologie">Virologie</option>
                </optgroup>
                  </select>
                </div>

                <!-- Bloc de localisation simulée -->
                <div class="col-md-6 mb-3 text-start">
                  <label class="form-label fw-bold">Lieu de l'intervention</label>
                  <select v-model="selectedLocation" class="form-select">
                    <option :value="{lat: 51.503, lon: -0.114}">Gare de Waterloo, Londres</option>
                    <option :value="{lat: 51.507, lon: -0.165}">Hyde Park, Londres</option>
                    <option :value="{lat: 51.523, lon: -0.073}">Shoreditch, Londres</option>
                    <option :value="{lat: 51.493, lon: -0.144}">Gare Victoria, Londres</option>
                  </select>
                </div>
              </div>

              <button @click="searchHospital" :disabled="loading" class="btn btn-primary w-100 fw-bold p-3 mt-2 shadow-sm">
                <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
                Trouver l'hôpital le plus proche
              </button>
              
            </div>
          </div>

          <!-- Affichage des résultats -->
          <div v-if="hospital" class="card result-card text-start border-success shadow animate__animated animate__fadeIn">
            <div class="card-body p-4">
              <div class="d-flex justify-content-between align-items-start">
                <h4 class="card-title text-success fw-bold">Établissement Recommandé</h4>
                <span class="badge bg-success p-2">Lit Réservé</span>
              </div>
              <hr>
              <h3 class="mb-3">{{ hospital.name }}</h3>
              
              <div class="row">
                <div class="col-6">
                  <p class="mb-1 text-muted small uppercase fw-bold">Capacité</p>
                  <p class="fs-5"><strong>{{ hospital.availableBeds }}</strong> lits disponibles</p>
                </div>
                <div class="col-6 border-start">
                  <p class="mb-1 text-muted small uppercase fw-bold">Temps de trajet estimé</p>
                  <p class="fs-5 text-primary">
                    <strong>{{ Math.round(hospital.travelTime / 60) }} minutes</strong>
                  </p>
                  </div>
              </div>
              
              <div class="alert alert-info mt-3 mb-0">
                <i class="bi bi-info-circle-fill me-2"></i>
                L'itinéraire a été calculé selon les conditions routières réelles.
              </div>
            </div>
          </div>

          <!-- Bouton de déconnexion -->
          <div class="mt-4 text-end">
             <button @click="logout" class="btn btn-outline-secondary btn-sm">Se déconnecter</button>
          </div>

        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

// États
const isAuthenticated = ref(false)
const loading = ref(false)
const usernameInput = ref('')
const passwordInput = ref('')
const error = ref('')

const specialism = ref('Médecine d\'urgence')
const selectedLocation = ref({ lat: 51.503, lon: -0.114 })
const hospital = ref(null)

// Fonctions
const login = () => {
  if (usernameInput.value && passwordInput.value) {
    isAuthenticated.value = true
    error.value = ''
  } else {
    error.value = "Veuillez saisir vos identifiants opérateurs."
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
    loading.ref = true
    error.value = ''
    hospital.value = null
    
    // Appel à l'API RESTful Java imposée [cite: 322]
    const response = await axios.get('http://localhost:8080/api/hospitals/nearest', {
      params: { 
        specialism: specialism.value, 
        lat: selectedLocation.value.lat, 
        lon: selectedLocation.value.lon 
      },
      auth: { 
        username: usernameInput.value, 
        password: passwordInput.value 
      }
    })
    
    hospital.value = response.data
  } catch (err) {
    if (err.response && err.response.status === 401) {
      error.value = "Session expirée ou identifiants invalides."
      isAuthenticated.value = false
    } else if (err.response && err.response.status === 404) {
      error.value = "Aucun hôpital disponible pour cette spécialité dans le périmètre actuel."
    } else {
      error.value = "Le service MedHead est momentanément indisponible."
    }
  } finally {
    loading.ref = false
  }
}
</script>
