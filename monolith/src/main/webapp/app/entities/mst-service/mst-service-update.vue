<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.mstService.home.createOrEditLabel"
          data-cy="MstServiceCreateUpdateHeading"
          v-text="t$('monolithApp.mstService.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="mstService.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="mstService.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstService.name')" for="mst-service-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="mst-service-name"
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
            <label class="form-control-label" v-text="t$('monolithApp.mstService.description')" for="mst-service-description"></label>
            <textarea
              class="form-control"
              name="description"
              id="mst-service-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstService.price')" for="mst-service-price"></label>
            <input
              type="number"
              class="form-control"
              name="price"
              id="mst-service-price"
              data-cy="price"
              :class="{ valid: !v$.price.$invalid, invalid: v$.price.$invalid }"
              v-model.number="v$.price.$model"
            />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.mstService.durationInHours')"
              for="mst-service-durationInHours"
            ></label>
            <input
              type="number"
              class="form-control"
              name="durationInHours"
              id="mst-service-durationInHours"
              data-cy="durationInHours"
              :class="{ valid: !v$.durationInHours.$invalid, invalid: v$.durationInHours.$invalid }"
              v-model.number="v$.durationInHours.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstService.serviceType')" for="mst-service-serviceType"></label>
            <select
              class="form-control"
              name="serviceType"
              :class="{ valid: !v$.serviceType.$invalid, invalid: v$.serviceType.$invalid }"
              v-model="v$.serviceType.$model"
              id="mst-service-serviceType"
              data-cy="serviceType"
            >
              <option
                v-for="serviceType in serviceTypeValues"
                :key="serviceType"
                v-bind:value="serviceType"
                v-bind:label="t$('monolithApp.ServiceType.' + serviceType)"
              >
                {{ serviceType }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstService.testimonial')" for="mst-service-testimonial"></label>
            <select
              class="form-control"
              id="mst-service-testimonial"
              data-cy="testimonial"
              name="testimonial"
              v-model="mstService.testimonial"
            >
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  mstService.testimonial && trxTestimonialOption.id === mstService.testimonial.id
                    ? mstService.testimonial
                    : trxTestimonialOption
                "
                v-for="trxTestimonialOption in trxTestimonials"
                :key="trxTestimonialOption.id"
              >
                {{ trxTestimonialOption.id }}
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
<script lang="ts" src="./mst-service-update.component.ts"></script>
