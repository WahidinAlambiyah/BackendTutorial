<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="jHipsterMonolithApp.country.home.createOrEditLabel"
          data-cy="CountryCreateUpdateHeading"
          v-text="t$('jHipsterMonolithApp.country.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="country.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="country.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.country.name')" for="country-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="country-name"
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
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.country.unm49Code')" for="country-unm49Code"></label>
            <input
              type="text"
              class="form-control"
              name="unm49Code"
              id="country-unm49Code"
              data-cy="unm49Code"
              :class="{ valid: !v$.unm49Code.$invalid, invalid: v$.unm49Code.$invalid }"
              v-model="v$.unm49Code.$model"
            />
            <div v-if="v$.unm49Code.$anyDirty && v$.unm49Code.$invalid">
              <small class="form-text text-danger" v-for="error of v$.unm49Code.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.country.isoAlpha2Code')" for="country-isoAlpha2Code"></label>
            <input
              type="text"
              class="form-control"
              name="isoAlpha2Code"
              id="country-isoAlpha2Code"
              data-cy="isoAlpha2Code"
              :class="{ valid: !v$.isoAlpha2Code.$invalid, invalid: v$.isoAlpha2Code.$invalid }"
              v-model="v$.isoAlpha2Code.$model"
            />
            <div v-if="v$.isoAlpha2Code.$anyDirty && v$.isoAlpha2Code.$invalid">
              <small class="form-text text-danger" v-for="error of v$.isoAlpha2Code.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.country.region')" for="country-region"></label>
            <select class="form-control" id="country-region" data-cy="region" name="region" v-model="country.region">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="country.region && regionOption.id === country.region.id ? country.region : regionOption"
                v-for="regionOption in regions"
                :key="regionOption.id"
              >
                {{ regionOption.name }}
              </option>
            </select>
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
<script lang="ts" src="./country-update.component.ts"></script>
