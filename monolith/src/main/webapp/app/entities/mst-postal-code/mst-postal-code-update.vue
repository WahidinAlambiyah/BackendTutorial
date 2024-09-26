<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.mstPostalCode.home.createOrEditLabel"
          data-cy="MstPostalCodeCreateUpdateHeading"
          v-text="t$('monolithApp.mstPostalCode.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="mstPostalCode.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="mstPostalCode.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstPostalCode.code')" for="mst-postal-code-code"></label>
            <input
              type="text"
              class="form-control"
              name="code"
              id="mst-postal-code-code"
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
              v-text="t$('monolithApp.mstPostalCode.subDistrict')"
              for="mst-postal-code-subDistrict"
            ></label>
            <select
              class="form-control"
              id="mst-postal-code-subDistrict"
              data-cy="subDistrict"
              name="subDistrict"
              v-model="mstPostalCode.subDistrict"
            >
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  mstPostalCode.subDistrict && mstSubDistrictOption.id === mstPostalCode.subDistrict.id
                    ? mstPostalCode.subDistrict
                    : mstSubDistrictOption
                "
                v-for="mstSubDistrictOption in mstSubDistricts"
                :key="mstSubDistrictOption.id"
              >
                {{ mstSubDistrictOption.name }}
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
<script lang="ts" src="./mst-postal-code-update.component.ts"></script>
