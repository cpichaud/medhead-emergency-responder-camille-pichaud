<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-6 text-center">
        
        <img src="./assets/logo.png" alt="Logo MedHead" class="logo-medhead">
        
        <h2 class="mb-4">Recherche d'Hôpital</h2>
        
        <div class="card shadow-sm mb-4">
          <div class="card-body">
            
            <div class="mb-3">
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

            <div class="row mb-3">
              <div class="col">
                <label class="form-label">Latitude</label>
                <input v-model="lat" type="number" step="any" class="form-control" />
              </div>
              <div class="col">
                <label class="form-label">Longitude</label>
                <input v-model="lon" type="number" step="any" class="form-control" />
              </div>
            </div>

            <button @click="searchHospital" class="btn btn-primary w-100 fw-bold">
              Trouver l'hôpital le plus proche
            </button>
            
          </div>
        </div>

        <div v-if="error" class="alert alert-danger">
          {{ error }}
        </div>

        <div v-if="hospital" class="card result-card text-start">
          <div class="card-body">
            <h4 class="card-title text-success">Intervention affectée</h4>
            <p class="card-text mb-1"><strong>Nom de l'établissement :</strong> {{ hospital.name }}</p>
            <p class="card-text"><strong>Lits disponibles :</strong> <span class="badge bg-success">{{ hospital.availableBeds }}</span></p>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const specialism = ref('Cardiologie')
const lat = ref(51.50)
const lon = ref(-0.12)
const hospital = ref(null)
const error = ref('')

const searchHospital = async () => {
  try {
    error.value = ''
    hospital.value = null
    
    const response = await axios.get('http://localhost:8080/api/hospitals/nearest', {
      params: { specialism: specialism.value, lat: lat.value, lon: lon.value },
      auth: { username: 'ambulance', password: 'root' }
    })
    
    hospital.value = response.data
  } catch (err) {
    if (err.response && err.response.status === 404) {
      error.value = "Aucun hôpital trouvé avec des lits disponibles pour cette spécialité."
    } else {
      error.value = "Erreur de connexion au serveur d'urgence."
    }
  }
}
</script>