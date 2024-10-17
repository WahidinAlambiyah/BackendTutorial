<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.mstDriver.home.createOrEditLabel"
          data-cy="MstDriverCreateUpdateHeading"
          v-text="t$('monolithApp.mstDriver.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="mstDriver.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="mstDriver.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstDriver.name')" for="mst-driver-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="mst-driver-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
              required
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstDriver.contactNumber')" for="mst-driver-contactNumber"></label>
            <input
              type="text"
              class="form-control"
              name="contactNumber"
              id="mst-driver-contactNumber"
              data-cy="contactNumber"
              :class="{ valid: !v$.contactNumber.$invalid, invalid: v$.contactNumber.$invalid }"
              v-model="v$.contactNumber.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstDriver.vehicleDetails')" for="mst-driver-vehicleDetails"></label>
            <input
              type="text"
              class="form-control"
              name="vehicleDetails"
              id="mst-driver-vehicleDetails"
              data-cy="vehicleDetails"
              :class="{ valid: !v$.vehicleDetails.$invalid, invalid: v$.vehicleDetails.$invalid }"
              v-model="v$.vehicleDetails.$model"
            />
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./mst-driver-update.component.ts"></script>
