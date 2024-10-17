<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.mstSupplier.home.createOrEditLabel"
          data-cy="MstSupplierCreateUpdateHeading"
          v-text="t$('monolithApp.mstSupplier.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="mstSupplier.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="mstSupplier.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstSupplier.name')" for="mst-supplier-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="mst-supplier-name"
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
            <label class="form-control-label" v-text="t$('monolithApp.mstSupplier.contactInfo')" for="mst-supplier-contactInfo"></label>
            <input
              type="text"
              class="form-control"
              name="contactInfo"
              id="mst-supplier-contactInfo"
              data-cy="contactInfo"
              :class="{ valid: !v$.contactInfo.$invalid, invalid: v$.contactInfo.$invalid }"
              v-model="v$.contactInfo.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstSupplier.address')" for="mst-supplier-address"></label>
            <input
              type="text"
              class="form-control"
              name="address"
              id="mst-supplier-address"
              data-cy="address"
              :class="{ valid: !v$.address.$invalid, invalid: v$.address.$invalid }"
              v-model="v$.address.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstSupplier.rating')" for="mst-supplier-rating"></label>
            <input
              type="number"
              class="form-control"
              name="rating"
              id="mst-supplier-rating"
              data-cy="rating"
              :class="{ valid: !v$.rating.$invalid, invalid: v$.rating.$invalid }"
              v-model.number="v$.rating.$model"
            />
            <div v-if="v$.rating.$anyDirty && v$.rating.$invalid">
              <small class="form-text text-danger" v-for="error of v$.rating.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
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
<script lang="ts" src="./mst-supplier-update.component.ts"></script>
