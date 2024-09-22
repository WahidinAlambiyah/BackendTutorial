<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="jHipsterMonolithApp.district.home.createOrEditLabel"
          data-cy="DistrictCreateUpdateHeading"
          v-text="t$('jHipsterMonolithApp.district.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="district.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="district.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.district.name')" for="district-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="district-name"
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
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.district.unm49Code')" for="district-unm49Code"></label>
            <input
              type="text"
              class="form-control"
              name="unm49Code"
              id="district-unm49Code"
              data-cy="unm49Code"
              :class="{ valid: !v$.unm49Code.$invalid, invalid: v$.unm49Code.$invalid }"
              v-model="v$.unm49Code.$model"
            />
            <div v-if="v$.unm49Code.$anyDirty && v$.unm49Code.$invalid">
              <small class="form-text text-danger" v-for="error of v$.unm49Code.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('jHipsterMonolithApp.district.isoAlpha2Code')"
              for="district-isoAlpha2Code"
            ></label>
            <input
              type="text"
              class="form-control"
              name="isoAlpha2Code"
              id="district-isoAlpha2Code"
              data-cy="isoAlpha2Code"
              :class="{ valid: !v$.isoAlpha2Code.$invalid, invalid: v$.isoAlpha2Code.$invalid }"
              v-model="v$.isoAlpha2Code.$model"
            />
            <div v-if="v$.isoAlpha2Code.$anyDirty && v$.isoAlpha2Code.$invalid">
              <small class="form-text text-danger" v-for="error of v$.isoAlpha2Code.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.district.city')" for="district-city"></label>
            <select class="form-control" id="district-city" data-cy="city" name="city" v-model="district.city">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="district.city && cityOption.id === district.city.id ? district.city : cityOption"
                v-for="cityOption in cities"
                :key="cityOption.id"
              >
                {{ cityOption.name }}
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
<script lang="ts" src="./district-update.component.ts"></script>
