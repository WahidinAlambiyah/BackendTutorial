<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="jHipsterMonolithApp.postalCode.home.createOrEditLabel"
          data-cy="PostalCodeCreateUpdateHeading"
          v-text="t$('jHipsterMonolithApp.postalCode.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="postalCode.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="postalCode.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.postalCode.code')" for="postal-code-code"></label>
            <input
              type="text"
              class="form-control"
              name="code"
              id="postal-code-code"
              data-cy="code"
              :class="{ valid: !v$.code.$invalid, invalid: v$.code.$invalid }"
              v-model="v$.code.$model"
              required
            />
            <div v-if="v$.code.$anyDirty && v$.code.$invalid">
              <small class="form-text text-danger" v-for="error of v$.code.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('jHipsterMonolithApp.postalCode.subDistrict')"
              for="postal-code-subDistrict"
            ></label>
            <select
              class="form-control"
              id="postal-code-subDistrict"
              data-cy="subDistrict"
              name="subDistrict"
              v-model="postalCode.subDistrict"
            >
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  postalCode.subDistrict && subDistrictOption.id === postalCode.subDistrict.id ? postalCode.subDistrict : subDistrictOption
                "
                v-for="subDistrictOption in subDistricts"
                :key="subDistrictOption.id"
              >
                {{ subDistrictOption.name }}
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
<script lang="ts" src="./postal-code-update.component.ts"></script>
