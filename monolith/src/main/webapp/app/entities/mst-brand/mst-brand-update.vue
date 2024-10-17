<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.mstBrand.home.createOrEditLabel"
          data-cy="MstBrandCreateUpdateHeading"
          v-text="t$('monolithApp.mstBrand.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="mstBrand.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="mstBrand.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstBrand.name')" for="mst-brand-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="mst-brand-name"
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
            <label class="form-control-label" v-text="t$('monolithApp.mstBrand.logo')" for="mst-brand-logo"></label>
            <input
              type="text"
              class="form-control"
              name="logo"
              id="mst-brand-logo"
              data-cy="logo"
              :class="{ valid: !v$.logo.$invalid, invalid: v$.logo.$invalid }"
              v-model="v$.logo.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstBrand.description')" for="mst-brand-description"></label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="mst-brand-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
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
<script lang="ts" src="./mst-brand-update.component.ts"></script>
